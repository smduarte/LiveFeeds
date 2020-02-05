package livefeeds.twister6;

import java.util.ArrayList;
import java.util.List;

import simsim.utils.Pair;

public class SlidingBitSet {

	public static int WORD_SIZE = 64; // long...

	public int base, top;
	public long[] bits;

	final public int maxSize;

	public SlidingBitSet() {
		this(Integer.MAX_VALUE);
	}

	public SlidingBitSet(int maxSize) {
		this.base = 0;
		this.top = -1;
		this.maxSize = maxSize >> 6;
		this.bits = new long[2];		
	}

	public SlidingBitSet(SlidingBitSet other) {
		this.top = other.top;
		this.base = other.base;
		this.maxSize = other.maxSize;

		this.bits = new long[other.bits.length];
		System.arraycopy(other.bits, 0, bits, 0, bits.length);
	}

	public SlidingBitSet clone() {
		return new SlidingBitSet(this);
	}

	public SlidingBitSet setBase(int b) {
		this.base = (b >>> 6) << 6;
		return this;
	}

	public int word8(int i) {
		long w = word(i >> 2);
		return (int) ((w >> (i & 3)) & 0xFF);
	}

	public void set(int b) {
		try {
			if (top < b)
				top = b;

			int i = b - base;
			if (i >= 0) {
				bits[i >> 6] |= masks[i & 63];

				while (bits[0] == ALL_ONES)
					slide();

			}
		} catch (Exception x) {
			grow();
			set(b);
		}
	}

	public boolean get(int b) {
		int j, i = b - base;
		return b >= 0 && (i < 0 || ((j = (i >> 6)) < bits.length && (bits[j] & masks[i & 63]) != 0));
	}

	public boolean contains(int b) {
		int j, i = b - base;
		return b >= 0 && (i < 0 || ((j = (i >> 6)) < bits.length && (bits[j] & masks[i & 63]) != 0));
	}

	public boolean equals(SlidingBitSet other) {
		int tB = this.base >> 6, oB = other.base >> 6;

		int b = Math.min(tB, oB);
		int B = Math.max(tB + this.bits.length, oB + other.bits.length);

		for (int i = b; i <= B; i++)
			if (this.word(i) != other.word(i))
				return false;
		return true;
	}

	public boolean contains(SlidingBitSet other) {
		int tB = this.base >> 6, oB = other.base >> 6;

		int b = Math.min(tB, oB);
		int B = Math.max(tB + this.bits.length, oB + other.bits.length);

		for (int i = b; i <= B; i++) {
			long tw = this.word(i), ow = other.word(i);
			if ((tw | ow) != tw)
				return false;
		}
		return true;
	}

	public List<Integer> difference(SlidingBitSet other) {
		List<Integer> res = new ArrayList<Integer>();
		int tB = this.base >> 6, oB = other.base >> 6;

		int b = Math.min(tB, oB);
		int B = Math.max(tB + this.bits.length, oB + other.bits.length);

		for (int i = b; i <= B; i++) {
			long diff = this.word(i) & ~other.word(i);
			if (diff != 0L)
				for (int j = 0; j < 64; j++)
					if ((diff & masks[j]) != 0L)
						res.add((i << 6) + j);
		}
		return res;
	}

	public Pair<List<Integer>, List<Integer>> mutualDifference(SlidingBitSet other) {

		List<Integer> this_minus_other = new ArrayList<Integer>();
		List<Integer> other_minus_this = new ArrayList<Integer>();

		int tB = this.base >> 6, oB = other.base >> 6;

		int b = Math.min(tB, oB);
		int B = Math.max(tB + this.bits.length, oB + other.bits.length);

		for (int i = b; i <= B; i++) {
			int i6 = i << 6;
			long tw = this.word(i), ow = other.word(i);

			long diff_tmo = tw & ~ow, diff_omt = ow & ~tw;

			for (int j = 0; j < 64; j++) {
				if ((diff_tmo & masks[j]) != 0L)
					this_minus_other.add(i6 + j);
				if ((diff_omt & masks[j]) != 0L)
					other_minus_this.add(i6 + j);
			}
		}
		return new Pair<List<Integer>, List<Integer>>(this_minus_other, other_minus_this);
	}

	private long word(int w) {
		w -= (base >> 6);
		return w < 0 ? ALL_ONES : (w >= bits.length ? 0L : bits[w]);
	}

	public String toString() {
		String res = "";
		for (int i = bits.length; --i >= 0;) {
			long v = bits[i];
			for (int j = 0; j < 64; j++) {
				res = ((v & 1) == 0 ? "0" : "1") + res;
				v >>>= 1;
			}
		}
		return String.format("<%5d : %s>", base, res);
	}

	final private void slide() {
		base += WORD_SIZE ;
		for (int j = 0; j < bits.length - 1; j++)
			bits[j] = bits[j + 1];
		bits[bits.length - 1] = 0L;
	}

	final private void grow2() {
		if (bits.length < maxSize) {
			long[] new_bits = new long[ 2 * bits.length];
			System.arraycopy(bits, 0, new_bits, 0, bits.length ) ;
			bits = new_bits;
		} else
			slide(); // cannot grow, forget the cleared bits in the lowest WORD.
	}

	final private void grow() {
		long[] new_bits = new long[ 2 * bits.length];
		System.arraycopy(bits, 0, new_bits, 0, bits.length ) ;
		bits = new_bits;
	}
	
	public int firstCleared() {
		for (int i = base + 1; i <= top; i++)
			if (!get(i))
				return i;

		return top + 1;
	}

	private static long[] masks = new long[64];
	private static final long ALL_ONES = 0xFFFFFFFFFFFFFFFFL;
	static {
		for (int i = 0; i < 64; i++)
			masks[i] = 0x8000000000000000L >>> i;
	}
}