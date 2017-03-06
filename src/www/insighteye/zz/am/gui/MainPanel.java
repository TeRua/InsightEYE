package www.insighteye.zz.am.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jsoup.nodes.Document;

import www.insighteye.zz.am.DaumSearchManager;
import www.insighteye.zz.am.NaverSearchManager;

public class MainPanel extends JPanel implements ActionListener {
	private JTextField tfKeyword;
	private JTextField tfPath;
	private JFileChooser jfc;
	private JButton btnPath;
	private JButton btnSearch;
	private DaumSearchManager dsm;
	private NaverSearchManager nsm;
	private JTextField tfSdate;
	private JTextField tfEdate;
	private JTextField tfTerm;
	private Document doc;

	private JCheckBox chkNaver;
	private JCheckBox chkDaum;
	private JCheckBox chkYeonhap;

	/**
	 * Create the panel.
	 */
	public MainPanel() {
		jfc = new JFileChooser();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 450, 0 };
		gridBagLayout.rowHeights = new int[] { 62, 68, 57, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JPanel searchPanel = new JPanel();
		GridBagConstraints gbc_searchPanel = new GridBagConstraints();
		gbc_searchPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchPanel.insets = new Insets(0, 0, 5, 0);
		gbc_searchPanel.gridx = 0;
		gbc_searchPanel.gridy = 0;
		add(searchPanel, gbc_searchPanel);
		GridBagLayout gbl_searchPanel = new GridBagLayout();
		gbl_searchPanel.columnWidths = new int[] { 134, 116, 121, 0 };
		gbl_searchPanel.rowHeights = new int[] { 27, 0, 0, 0 };
		gbl_searchPanel.columnWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_searchPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		searchPanel.setLayout(gbl_searchPanel);

		JLabel lblKeyword = new JLabel("Keyword");
		GridBagConstraints gbc_lblKeyword = new GridBagConstraints();
		gbc_lblKeyword.insets = new Insets(0, 0, 5, 5);
		gbc_lblKeyword.gridx = 0;
		gbc_lblKeyword.gridy = 0;
		searchPanel.add(lblKeyword, gbc_lblKeyword);

		tfKeyword = new JTextField();
		GridBagConstraints gbc_tfKeyword = new GridBagConstraints();
		gbc_tfKeyword.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKeyword.insets = new Insets(0, 0, 5, 5);
		gbc_tfKeyword.gridx = 1;
		gbc_tfKeyword.gridy = 0;
		searchPanel.add(tfKeyword, gbc_tfKeyword);
		tfKeyword.setColumns(10);

		btnSearch = new JButton("검색");
		btnSearch.addActionListener(this);
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 5, 0);
		gbc_btnSearch.anchor = GridBagConstraints.NORTH;
		gbc_btnSearch.gridx = 2;
		gbc_btnSearch.gridy = 0;
		searchPanel.add(btnSearch, gbc_btnSearch);

		JPanel datePanel = new JPanel();
		GridBagConstraints gbc_datePanel = new GridBagConstraints();
		gbc_datePanel.insets = new Insets(0, 0, 5, 0);
		gbc_datePanel.gridwidth = 3;
		gbc_datePanel.fill = GridBagConstraints.BOTH;
		gbc_datePanel.gridx = 0;
		gbc_datePanel.gridy = 1;
		searchPanel.add(datePanel, gbc_datePanel);
		GridBagLayout gbl_datePanel = new GridBagLayout();
		gbl_datePanel.columnWidths = new int[] { 191, 147, 126, 0 };
		gbl_datePanel.rowHeights = new int[] { 18, 0, 0 };
		gbl_datePanel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_datePanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		datePanel.setLayout(gbl_datePanel);

		JLabel lblSdate = new JLabel("Start");
		GridBagConstraints gbc_lblSdate = new GridBagConstraints();
		gbc_lblSdate.anchor = GridBagConstraints.NORTH;
		gbc_lblSdate.insets = new Insets(0, 0, 5, 5);
		gbc_lblSdate.gridx = 0;
		gbc_lblSdate.gridy = 0;
		datePanel.add(lblSdate, gbc_lblSdate);

		JLabel lblEdate = new JLabel("End");
		GridBagConstraints gbc_lblEdate = new GridBagConstraints();
		gbc_lblEdate.anchor = GridBagConstraints.NORTH;
		gbc_lblEdate.insets = new Insets(0, 0, 5, 5);
		gbc_lblEdate.gridx = 1;
		gbc_lblEdate.gridy = 0;
		datePanel.add(lblEdate, gbc_lblEdate);

		JLabel lblTerm = new JLabel("Term");
		GridBagConstraints gbc_lblTerm = new GridBagConstraints();
		gbc_lblTerm.insets = new Insets(0, 0, 5, 0);
		gbc_lblTerm.anchor = GridBagConstraints.NORTH;
		gbc_lblTerm.gridx = 2;
		gbc_lblTerm.gridy = 0;
		datePanel.add(lblTerm, gbc_lblTerm);

		tfSdate = new JTextField();
		tfSdate.setText("yyyymmdd");
		GridBagConstraints gbc_tfSdate = new GridBagConstraints();
		gbc_tfSdate.insets = new Insets(0, 0, 0, 5);
		gbc_tfSdate.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSdate.gridx = 0;
		gbc_tfSdate.gridy = 1;
		datePanel.add(tfSdate, gbc_tfSdate);
		tfSdate.setColumns(10);

		tfEdate = new JTextField();
		tfEdate.setText("yyyymmdd");
		tfEdate.setColumns(10);
		GridBagConstraints gbc_tfEdate = new GridBagConstraints();
		gbc_tfEdate.insets = new Insets(0, 0, 0, 5);
		gbc_tfEdate.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfEdate.gridx = 1;
		gbc_tfEdate.gridy = 1;
		datePanel.add(tfEdate, gbc_tfEdate);

		tfTerm = new JTextField();
		tfTerm.setText("1~360");
		tfTerm.setColumns(10);
		GridBagConstraints gbc_tfTerm = new GridBagConstraints();
		gbc_tfTerm.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTerm.gridx = 2;
		gbc_tfTerm.gridy = 1;
		datePanel.add(tfTerm, gbc_tfTerm);

		JPanel targetPanel = new JPanel();
		GridBagConstraints gbc_targetPanel = new GridBagConstraints();
		gbc_targetPanel.fill = GridBagConstraints.BOTH;
		gbc_targetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_targetPanel.gridx = 0;
		gbc_targetPanel.gridy = 1;
		add(targetPanel, gbc_targetPanel);
		GridBagLayout gbl_targetPanel = new GridBagLayout();
		gbl_targetPanel.columnWidths = new int[] { 65, 65, 57, 0 };
		gbl_targetPanel.rowHeights = new int[] { 27, 0 };
		gbl_targetPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_targetPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		targetPanel.setLayout(gbl_targetPanel);

		chkNaver = new JCheckBox("Naver");
		GridBagConstraints gbc_chkNaver = new GridBagConstraints();
		gbc_chkNaver.insets = new Insets(0, 0, 0, 5);
		gbc_chkNaver.gridx = 0;
		gbc_chkNaver.gridy = 0;
		targetPanel.add(chkNaver, gbc_chkNaver);

		chkDaum = new JCheckBox("Daum");
		GridBagConstraints gbc_chkDaum = new GridBagConstraints();
		gbc_chkDaum.insets = new Insets(0, 0, 0, 5);
		gbc_chkDaum.gridx = 1;
		gbc_chkDaum.gridy = 0;
		targetPanel.add(chkDaum, gbc_chkDaum);

		chkYeonhap = new JCheckBox("연합");
		GridBagConstraints gbc_chkYonhap = new GridBagConstraints();
		gbc_chkYonhap.gridx = 2;
		gbc_chkYonhap.gridy = 0;
		targetPanel.add(chkYeonhap, gbc_chkYonhap);

		JPanel pathPanel = new JPanel();
		GridBagConstraints gbc_pathPanel = new GridBagConstraints();
		gbc_pathPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_pathPanel.gridx = 0;
		gbc_pathPanel.gridy = 2;
		add(pathPanel, gbc_pathPanel);
		GridBagLayout gbl_pathPanel = new GridBagLayout();
		gbl_pathPanel.columnWidths = new int[] { 124, 28, 116, 0 };
		gbl_pathPanel.rowHeights = new int[] { 27, 0 };
		gbl_pathPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_pathPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pathPanel.setLayout(gbl_pathPanel);

		JLabel lblPath = new JLabel("경로");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.insets = new Insets(0, 0, 0, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 0;
		pathPanel.add(lblPath, gbc_lblPath);

		tfPath = new JTextField();
		tfPath.setEditable(false);
		GridBagConstraints gbc_tfPath = new GridBagConstraints();
		gbc_tfPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPath.insets = new Insets(0, 0, 0, 5);
		gbc_tfPath.gridx = 1;
		gbc_tfPath.gridy = 0;
		pathPanel.add(tfPath, gbc_tfPath);
		tfPath.setColumns(10);

		btnPath = new JButton("...");
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setAcceptAllFileFilterUsed(false);
		btnPath.addActionListener(this);
		GridBagConstraints gbc_btnPath = new GridBagConstraints();
		gbc_btnPath.anchor = GridBagConstraints.NORTH;
		gbc_btnPath.gridx = 2;
		gbc_btnPath.gridy = 0;
		pathPanel.add(btnPath, gbc_btnPath);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnPath) {
			if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				tfPath.setText(jfc.getSelectedFile().toString());
			}
		} else if (e.getSource() == btnSearch) {
			if (chkNaver.isSelected()) {
				System.out.println("test1");
				Thread th1 = new Thread() {
					@Override
					public void run() {
						nsm = new NaverSearchManager(tfSdate.getText(), tfEdate.getText(),
								Integer.parseInt(tfTerm.getText()));
						nsm.setPath(tfPath.getText());
						Document doc1 = nsm.search(tfKeyword.getText());
						while (doc1 != null) {
							doc1 = nsm.search();
						}
					}
				};
				th1.start();
			}
			if (chkDaum.isSelected()) {
				System.out.println("test2");
				Thread th2 = new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						dsm = new DaumSearchManager(tfSdate.getText(), tfEdate.getText(),
								Integer.parseInt(tfTerm.getText()));
						dsm.setPath(tfPath.getText());
						doc = dsm.search(tfKeyword.getText());
						while (doc != null) {
							doc = dsm.search();
						}
					}
				};
				th2.start();
			}

		}
	}

}
