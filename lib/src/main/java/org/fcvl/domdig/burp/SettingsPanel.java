package org.fcvl.domdig.burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.json.JSONObject;

public class SettingsPanel extends JPanel {
	private DomdigUI parent;
	private JTextField nodePathTextField;
	private JTextField domdigPathTextField;
	private TableEditor cookiesEditor;
	private KeyValueEditor kvEditHeaders;
	private JTextField httpAuthUserTextField;
	private JTextField httpAuthPassTextField;
	private JTextArea loginSequenceTextArea;
	private JTextField ignoreRegexTextField;
	private JTextField payloadFileTextField;
	private KeyValueEditor kvEditLocalstorage;
	private JCheckBox tplInjCheckBox;
	private JCheckBox modeDOMScanCheckBox;
	private JCheckBox modeFuzzCheckBox;
	private JCheckBox storedXSSCheckBox;
	
	private JTextField proxyTextField;
	private JCheckBox proxyCheckBox;
	private JScrollPane mainGridScrollPane;
	private JCheckBox singleBrowserCheckBox;
	private JTextField timeoutTextField;
	
	public DomdigExecutor getExecutor(String targetUrl) {
		String credentials = httpAuthUserTextField.getText() + ":" + httpAuthPassTextField.getText();
		ArrayList<String> modes = new ArrayList<>();

		if(modeDOMScanCheckBox.isSelected()) {
			modes.add("domscan");
		}
		if(modeFuzzCheckBox.isSelected()) {
			modes.add("fuzz");
		}
			
		String proxy = proxyCheckBox.isSelected() ? proxyTextField.getText() : "";
		
		int timeout = 0;
		try {
			timeout = Integer.parseInt(timeoutTextField.getText()) * 1000;
		} catch(Exception e) {
			alertError("Invalid timeout: " + timeoutTextField.getText());
			return null;
		}

		File nodeExe = new File(nodePathTextField.getText());
		if(!nodeExe.exists()) {
			alertError("Node executable not found");
			return null;
		}
		if(nodeExe.isDirectory()) {
			alertError("Node executable is a directory");
			return null;
		}

		File domdigExe = new File(domdigPathTextField.getText());
		if(!domdigExe.exists() || domdigExe.isDirectory()) {
			alertError("Domdig.js not found");
			return null;
		}
		
		return new DomdigExecutor(nodeExe.getAbsolutePath(), domdigExe.getAbsolutePath(), 
				targetUrl, cookiesEditor.toJson(), credentials, timeout,
				proxy, kvEditHeaders.toJson(),  loginSequenceTextArea.getText(), payloadFileTextField.getText(),
				String.join(",", modes), ignoreRegexTextField.getText(), kvEditLocalstorage.toJson(), tplInjCheckBox.isSelected(), storedXSSCheckBox.isSelected(), !singleBrowserCheckBox.isSelected());
	}

	private void alertError(String message) {
		parent.showAlert(message, true);
	}
	
	public String getJson() {
		JSONObject json = new JSONObject();
		json.put("tplInjCheckBox", tplInjCheckBox.isSelected());
		json.put("modeDOMScanCheckBox", modeDOMScanCheckBox.isSelected());
		json.put("modeFuzzCheckBox", modeFuzzCheckBox.isSelected());
		json.put("storedXSSCheckBox", storedXSSCheckBox.isSelected());
		json.put("proxyCheckBox", proxyCheckBox.isSelected());
		json.put("nodePathTextField", nodePathTextField.getText());
		json.put("domdigPathTextField", domdigPathTextField.getText());
		json.put("httpAuthUserTextField", httpAuthUserTextField.getText());
		json.put("httpAuthPassTextField", httpAuthPassTextField.getText());
		json.put("ignoreRegexTextField", ignoreRegexTextField.getText());
		json.put("payloadFileTextField", payloadFileTextField.getText());
		json.put("proxyTextField", proxyTextField.getText());
		json.put("loginSequenceTextArea", loginSequenceTextArea.getText());
		json.put("cookiesEditor", cookiesEditor.toJson());
		json.put("kvEditHeaders", kvEditHeaders.toJson());
		json.put("kvEditLocalstorage", kvEditLocalstorage.toJson());
		json.put("singleBrowserCheckBox", singleBrowserCheckBox.isSelected());
		json.put("timeoutTextField", timeoutTextField.getText());
		return json.toString();
		
	}

	public void loadJson(String j) {
		JSONObject json = new JSONObject(j);
		loginSequenceTextArea.setText(json.getString("loginSequenceTextArea"));
		httpAuthUserTextField.setText(json.getString("httpAuthUserTextField"));
		nodePathTextField.setText(json.getString("nodePathTextField"));
		nodePathTextField.setText(json.getString("nodePathTextField"));
		ignoreRegexTextField.setText(json.getString("ignoreRegexTextField"));
		payloadFileTextField.setText(json.getString("payloadFileTextField"));
		domdigPathTextField.setText(json.getString("domdigPathTextField"));
		proxyTextField.setText(json.getString("proxyTextField"));
		cookiesEditor.fromJson(json.getString("cookiesEditor"));
		kvEditHeaders.fromJson(json.getString("kvEditHeaders"));
		kvEditLocalstorage.fromJson(json.getString("kvEditLocalstorage"));
		tplInjCheckBox.setSelected(json.getBoolean("tplInjCheckBox"));
		modeDOMScanCheckBox.setSelected(json.getBoolean("modeDOMScanCheckBox"));
		modeFuzzCheckBox.setSelected(json.getBoolean("modeFuzzCheckBox"));
		storedXSSCheckBox.setSelected(json.getBoolean("storedXSSCheckBox"));
		proxyCheckBox.setSelected(json.getBoolean("proxyCheckBox"));
		singleBrowserCheckBox.setSelected(json.getBoolean("singleBrowserCheckBox"));
		timeoutTextField.setText(json.getString("timeoutTextField"));
	}

	public void setEnabledComponents(Boolean enabled) {
		System.out.println("enable");
		loginSequenceTextArea.setEnabled(enabled);
		httpAuthUserTextField.setEnabled(enabled);
		nodePathTextField.setEnabled(enabled);
		nodePathTextField.setEnabled(enabled);
		ignoreRegexTextField.setEnabled(enabled);
		payloadFileTextField.setEnabled(enabled);
		domdigPathTextField.setEnabled(enabled);
		proxyTextField.setEnabled(enabled);
		cookiesEditor.setEnabled(enabled);
		kvEditHeaders.setEnabled(enabled);
		kvEditLocalstorage.setEnabled(enabled);
		tplInjCheckBox.setEnabled(enabled);
		modeDOMScanCheckBox.setEnabled(enabled);
		modeFuzzCheckBox.setEnabled(enabled);
		storedXSSCheckBox.setEnabled(enabled);
		proxyCheckBox.setEnabled(enabled);
		singleBrowserCheckBox.setEnabled(enabled);
	}
	
	public Boolean checkScannerIsConfigured() {
		  if(domdigPathTextField.getText().equals("") || nodePathTextField.getText().equals("")) {
			  mainGridScrollPane.getVerticalScrollBar().setValue(10000);
			  alertError("Please set Node's path and DomDig's path");
			  return false;
		  }
		  return true;
	}
	
	public String selectFile(){
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			return selectedFile.getAbsolutePath();
		}
		return null;
	}

	public SettingsPanel(DomdigUI parent) {
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		
		mainGridScrollPane = new JScrollPane();
		mainGridScrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		mainGridScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(mainGridScrollPane, BorderLayout.CENTER);
		
		JPanel mainGridPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) mainGridPanel.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setVgap(0);
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setAlignment(FlowLayout.LEFT);
		mainGridScrollPane.setViewportView(mainGridPanel);
		
		JPanel mainGrid = new JPanel();
		mainGridPanel.add(mainGrid);
		GridBagLayout gbl_mainGrid = new GridBagLayout();
		gbl_mainGrid.columnWidths = new int[]{0, 0};
		gbl_mainGrid.rowHeights = new int[]{0, 0, 0, 0};
		gbl_mainGrid.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_mainGrid.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainGrid.setLayout(gbl_mainGrid);
		
		JPanel settingsPanel_1 = new JPanel();
		settingsPanel_1.setBorder(new TitledBorder(null, "Scan Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_settingsPanel_1 = new GridBagConstraints();
		gbc_settingsPanel_1.ipady = 5;
		gbc_settingsPanel_1.ipadx = 5;
		gbc_settingsPanel_1.insets = new Insets(0, 0, 5, 0);
		gbc_settingsPanel_1.gridx = 0;
		gbc_settingsPanel_1.gridy = 0;
		mainGrid.add(settingsPanel_1, gbc_settingsPanel_1);
		GridBagLayout gbl_settingsPanel_1 = new GridBagLayout();
		gbl_settingsPanel_1.columnWidths = new int[]{0, 0, 0, 0};
		gbl_settingsPanel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_settingsPanel_1.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_settingsPanel_1.rowWeights = new double[]{1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		settingsPanel_1.setLayout(gbl_settingsPanel_1);
		
		JLabel lblNewLabel_5 = new JLabel("Modes");
		lblNewLabel_5.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.ipady = 13;
		gbc_lblNewLabel_5.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 0;
		settingsPanel_1.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_1.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		settingsPanel_1.add(panel_1, gbc_panel_1);
		
		modeDOMScanCheckBox = new JCheckBox("DOM Scan");
		modeDOMScanCheckBox.setSelected(true);
		panel_1.add(modeDOMScanCheckBox);
		
		modeFuzzCheckBox = new JCheckBox("Fuzz");
		modeFuzzCheckBox.setSelected(true);
		panel_1.add(modeFuzzCheckBox);
		
		JLabel lblNewLabel_14 = new JLabel("Additional Checks");
		lblNewLabel_14.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_14 = new GridBagConstraints();
		gbc_lblNewLabel_14.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_14.gridx = 0;
		gbc_lblNewLabel_14.gridy = 1;
		settingsPanel_1.add(lblNewLabel_14, gbc_lblNewLabel_14);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_3.getLayout();
		flowLayout_3.setVgap(0);
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 1;
		settingsPanel_1.add(panel_3, gbc_panel_3);
		
		tplInjCheckBox = new JCheckBox("Template Injection");
		panel_3.add(tplInjCheckBox);
		tplInjCheckBox.setSelected(true);
		
		storedXSSCheckBox = new JCheckBox("Stored XSS");
		panel_3.add(storedXSSCheckBox);
		storedXSSCheckBox.setSelected(true);
		
		JLabel lblNewLabel_11 = new JLabel("Browser");
		lblNewLabel_11.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_11 = new GridBagConstraints();
		gbc_lblNewLabel_11.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_11.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_11.gridx = 0;
		gbc_lblNewLabel_11.gridy = 2;
		settingsPanel_1.add(lblNewLabel_11, gbc_lblNewLabel_11);
		
		singleBrowserCheckBox = new JCheckBox("Use a new browser for every new URL");
		singleBrowserCheckBox.setSelected(true);
		GridBagConstraints gbc_singleBrowserCheckBox = new GridBagConstraints();
		gbc_singleBrowserCheckBox.anchor = GridBagConstraints.WEST;
		gbc_singleBrowserCheckBox.insets = new Insets(5, 5, 5, 5);
		gbc_singleBrowserCheckBox.gridx = 1;
		gbc_singleBrowserCheckBox.gridy = 2;
		settingsPanel_1.add(singleBrowserCheckBox, gbc_singleBrowserCheckBox);
		
		JLabel lblNewLabel_10 = new JLabel("Proxy");
		lblNewLabel_10.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_10 = new GridBagConstraints();
		gbc_lblNewLabel_10.ipady = 16;
		gbc_lblNewLabel_10.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_10.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_10.gridx = 0;
		gbc_lblNewLabel_10.gridy = 3;
		settingsPanel_1.add(lblNewLabel_10, gbc_lblNewLabel_10);
		
		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) panel_6.getLayout();
		flowLayout_5.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.insets = new Insets(0, 0, 5, 5);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 1;
		gbc_panel_6.gridy = 3;
		settingsPanel_1.add(panel_6, gbc_panel_6);
		
		proxyTextField = new JTextField();
		proxyTextField.setText("http:127.0.0.1:8080");
		panel_6.add(proxyTextField);
		proxyTextField.setColumns(13);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		panel_6.add(separator_2);
		
		proxyCheckBox = new JCheckBox("Enabled");
		proxyCheckBox.setSelected(true);
		panel_6.add(proxyCheckBox);
		
		JLabel lblNewLabel_2_1 = new JLabel("Cookies");
		lblNewLabel_2_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_2_1 = new GridBagConstraints();
		gbc_lblNewLabel_2_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_2_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2_1.gridx = 0;
		gbc_lblNewLabel_2_1.gridy = 4;
		settingsPanel_1.add(lblNewLabel_2_1, gbc_lblNewLabel_2_1);
		
		cookiesEditor = new TableEditor(new String[] {"name", "value", "domain", "path"}, new Integer[] {200, 400, 200, 200});
		JPanel jp = new JPanel();
		GridBagConstraints gbc_jp = new GridBagConstraints();
		gbc_jp.fill = GridBagConstraints.BOTH;
		gbc_jp.insets = new Insets(0, 0, 5, 5);
		gbc_jp.gridx = 1;
		gbc_jp.gridy = 4;
		settingsPanel_1.add(cookiesEditor, gbc_jp);
		
		JLabel lblNewLabel_1_1 = new JLabel("Headers");
		lblNewLabel_1_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_1_1 = new GridBagConstraints();
		gbc_lblNewLabel_1_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_1_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1_1.gridx = 0;
		gbc_lblNewLabel_1_1.gridy = 5;
		settingsPanel_1.add(lblNewLabel_1_1, gbc_lblNewLabel_1_1);
		
		kvEditHeaders = new KeyValueEditor();
		
		GridBagConstraints gbc_kvEditHeaders = new GridBagConstraints();
		gbc_kvEditHeaders.fill = GridBagConstraints.HORIZONTAL;
		gbc_kvEditHeaders.insets = new Insets(0, 0, 5, 5);
		gbc_kvEditHeaders.gridx = 1;
		gbc_kvEditHeaders.gridy = 5;
		settingsPanel_1.add(kvEditHeaders, gbc_kvEditHeaders);
		
		JLabel lblNewLabel_9 = new JLabel("Local Storage");
		lblNewLabel_9.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_9.gridx = 0;
		gbc_lblNewLabel_9.gridy = 6;
		settingsPanel_1.add(lblNewLabel_9, gbc_lblNewLabel_9);
		
		kvEditLocalstorage = new KeyValueEditor();
		GridBagConstraints gbc_kvEditLocalstorage = new GridBagConstraints();
		gbc_kvEditLocalstorage.fill = GridBagConstraints.HORIZONTAL;
		gbc_kvEditLocalstorage.insets = new Insets(0, 0, 5, 5);
		gbc_kvEditLocalstorage.gridx = 1;
		gbc_kvEditLocalstorage.gridy = 6;
		settingsPanel_1.add(kvEditLocalstorage, gbc_kvEditLocalstorage);
		
		JLabel lblNewLabel = new JLabel("HTTP Auth");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.ipady = 20;
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 7;
		settingsPanel_1.add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setAlignOnBaseline(true);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 7;
		settingsPanel_1.add(panel, gbc_panel);
		
		JLabel lblNewLabel_3 = new JLabel("user:");
		panel.add(lblNewLabel_3);
		
		httpAuthUserTextField = new JTextField();
		panel.add(httpAuthUserTextField);
		httpAuthUserTextField.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("pass:");
		panel.add(lblNewLabel_4);
		
		httpAuthPassTextField = new JTextField();
		panel.add(httpAuthPassTextField);
		httpAuthPassTextField.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("Login Sequence");
		lblNewLabel_6.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 8;
		settingsPanel_1.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 8;
		settingsPanel_1.add(panel_2, gbc_panel_2);
		panel_2.setLayout(new GridLayout(1, 0, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane);
		
		loginSequenceTextArea = new JTextArea();
		scrollPane.setViewportView(loginSequenceTextArea);
		
		JTextArea txtrtypeusername = new JTextArea();
		txtrtypeusername.setText("  [\n      [\"navigate\", \"https://loginpage.local\"],\n      [\"write\", \"#username\", \"example\"],\n      [\"write\", \"#password\", \"example\"],\n      [\"sleep\", 3],\n      [\"clickToNavigate\", \"#btn-login\"]\n  ]");
		txtrtypeusername.setBackground(SystemColor.window);
		panel_2.add(txtrtypeusername);
		
		JLabel lblNewLabel_8 = new JLabel("Payload file");
		lblNewLabel_8.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.ipady = 18;
		gbc_lblNewLabel_8.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 9;
		settingsPanel_1.add(lblNewLabel_8, gbc_lblNewLabel_8);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panel_5.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(0, 0, 5, 5);
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.gridx = 1;
		gbc_panel_5.gridy = 9;
		settingsPanel_1.add(panel_5, gbc_panel_5);
		
		payloadFileTextField = new JTextField();
		panel_5.add(payloadFileTextField);
		payloadFileTextField.setColumns(33);
		
		JSeparator separator_1 = new JSeparator();
		panel_5.add(separator_1);
		
		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String f = selectFile();
				if(f != null)
					payloadFileTextField.setText(f);
			}
		});
		panel_5.add(btnNewButton);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		panel_5.add(separator);
		
		JLabel lblNewLabel_7 = new JLabel("Ignore RegEx");
		lblNewLabel_7.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.ipady = 5;
		gbc_lblNewLabel_7.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 10;
		settingsPanel_1.add(lblNewLabel_7, gbc_lblNewLabel_7);
		
		ignoreRegexTextField = new JTextField();
		ignoreRegexTextField.setText(".*logout.*");
		GridBagConstraints gbc_ignoreRegexTextField = new GridBagConstraints();
		gbc_ignoreRegexTextField.insets = new Insets(0, 0, 5, 5);
		gbc_ignoreRegexTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ignoreRegexTextField.gridx = 1;
		gbc_ignoreRegexTextField.gridy = 10;
		settingsPanel_1.add(ignoreRegexTextField, gbc_ignoreRegexTextField);
		ignoreRegexTextField.setColumns(10);
		
		JLabel lblNewLabel_12 = new JLabel("Timeout");
		lblNewLabel_12.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_12 = new GridBagConstraints();
		gbc_lblNewLabel_12.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_12.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_12.gridx = 0;
		gbc_lblNewLabel_12.gridy = 11;
		settingsPanel_1.add(lblNewLabel_12, gbc_lblNewLabel_12);
		
		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) panel_4.getLayout();
		flowLayout_6.setAlignment(FlowLayout.LEFT);
		flowLayout_6.setVgap(0);
		flowLayout_6.setHgap(0);
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 0, 5);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 11;
		settingsPanel_1.add(panel_4, gbc_panel_4);
		
		timeoutTextField = new JTextField();
		timeoutTextField.setText("30");
		panel_4.add(timeoutTextField);
		timeoutTextField.setColumns(5);
		
		JLabel lblNewLabel_13 = new JLabel("  seconds");
		panel_4.add(lblNewLabel_13);
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(new TitledBorder(null, "Scanner Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_settingsPanel = new GridBagConstraints();
		gbc_settingsPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_settingsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_settingsPanel.gridx = 0;
		gbc_settingsPanel.gridy = 1;
		mainGrid.add(settingsPanel, gbc_settingsPanel);
		GridBagLayout gbl_settingsPanel = new GridBagLayout();
		gbl_settingsPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_settingsPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_settingsPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_settingsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		settingsPanel.setLayout(gbl_settingsPanel);
		
		JLabel lblNewLabel_2 = new JLabel("Node Path");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 0;
		settingsPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		nodePathTextField = new JTextField();
		nodePathTextField.setColumns(10);
		GridBagConstraints gbc_nodePathTextField = new GridBagConstraints();
		gbc_nodePathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nodePathTextField.insets = new Insets(0, 0, 5, 5);
		gbc_nodePathTextField.gridx = 1;
		gbc_nodePathTextField.gridy = 0;
		settingsPanel.add(nodePathTextField, gbc_nodePathTextField);
		
		JButton btnNewButton_3 = new JButton("...");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String f = selectFile();
				if(f != null)
					nodePathTextField.setText(f);
			}
		});
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_3.gridx = 2;
		gbc_btnNewButton_3.gridy = 0;
		settingsPanel.add(btnNewButton_3, gbc_btnNewButton_3);
		
		JLabel lblNewLabel_1 = new JLabel("Domdig Path");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		settingsPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		domdigPathTextField = new JTextField();
		domdigPathTextField.setColumns(30);
		GridBagConstraints gbc_domdigPathTextField = new GridBagConstraints();
		gbc_domdigPathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_domdigPathTextField.insets = new Insets(0, 0, 0, 5);
		gbc_domdigPathTextField.gridx = 1;
		gbc_domdigPathTextField.gridy = 1;
		settingsPanel.add(domdigPathTextField, gbc_domdigPathTextField);
		
		JButton btnNewButton_1 = new JButton("...");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String f = selectFile();
				if(f != null)
					domdigPathTextField.setText(f);
			}
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 2;
		gbc_btnNewButton_1.gridy = 1;
		settingsPanel.add(btnNewButton_1, gbc_btnNewButton_1);
		
		JLabel domdigLinkLabel = new JLabel("https://github.com/fcavallarin/domdig");
		domdigLinkLabel.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		domdigLinkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				try {
					Desktop.getDesktop().browse(new URI(domdigLinkLabel.getText()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		domdigLinkLabel.setForeground(new Color(4, 97, 255));
		GridBagConstraints gbc_domdigLinkLabel = new GridBagConstraints();
		gbc_domdigLinkLabel.anchor = GridBagConstraints.WEST;
		gbc_domdigLinkLabel.insets = new Insets(0, 0, 0, 5);
		gbc_domdigLinkLabel.gridx = 1;
		gbc_domdigLinkLabel.gridy = 2;
		settingsPanel.add(domdigLinkLabel, gbc_domdigLinkLabel);

		mainGridScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				mainGridScrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}
}
