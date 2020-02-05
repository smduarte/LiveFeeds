package simsim.gui;

import javax.swing.JInternalFrame;

@SuppressWarnings("serial")
class IFrame extends JInternalFrame {

	IPanel panel;

	IFrame( String title, IPanel p ) {
		this.panel = p ;
		
		this.setTitle(title);
		this.setClosable(true);
		this.setResizable(true);
		this.setMaximizable(true);

		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));
		add(panel);

		this.pack();
		this.setVisible(true);
	}


}
