package sensing.persistence.core.network

;

public class Tree {
	def root;
	def parent = [:];
	def children = [:];

	public Tree(root) {
		this.root = root;
	}

	public void addChild(p, child) {
		if(children[p] == null) {
			children[p] = [];
		}
		children[p] << child;
		parent[child] = p;
	}
}
