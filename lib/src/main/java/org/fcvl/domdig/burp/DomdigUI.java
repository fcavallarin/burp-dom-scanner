package org.fcvl.domdig.burp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.persistence.PersistedObject;


public class DomdigUI extends JPanel {
	private MontoyaApi burpApi;
	
	private JTextField targetUrlTextField;
	private DomdigExecutor executor = null;
	private JLabel statusLabel;
	private SettingsPanel settingsPanel;
	private JCheckBox dryRunCheckBox;
	private JButton btnToggleScan;
	private CrawlResultsPanel crawlResultsPanel;
	private VulnerabilitiesPanel vulnerabilitiesPanel;

	
	private void startScan() {
		
		if(!settingsPanel.checkScannerIsConfigured()) {
			return;
		}
		//System.out.println(settingsPanel.getJson());
		saveState();
		if(executor == null || !executor.isRunning()) {
			executor = settingsPanel.getExecutor(targetUrlTextField.getText());
			if(executor == null) {
				return;
			}

			btnToggleScan.setText("Stop Scan");
			btnToggleScan.setEnabled(true);
			crawlResultsPanel.reset();
			vulnerabilitiesPanel.reset();
			targetUrlTextField.setEditable(false);
			settingsPanel.setEnabledComponents(false);
			dryRunCheckBox.setEnabled(false);

			DomdigEvents events = new DomdigEvents(){
				@Override
				public void onError(String message) {
					showAlert(message, true);
				}
				@Override
				public void onStatusChange(String message) {
					statusLabel.setText(message);
					//System.out.println("TIK " + message);
				}
				@Override
				public void onNewRequests(ArrayList<DomdigRequest> reqList) {
					crawlResultsPanel.loadRequestsList(reqList);
				}
				@Override
				public void onNewVulnerabilities(ArrayList<DomdigVulnerability> vulnList) {
					vulnerabilitiesPanel.loadVulnerabilitiesList(vulnList);
				}
				@Override
				public void onScanCompleted(Boolean error) {
					btnToggleScan.setText("Start Scan");
					btnToggleScan.setEnabled(true);
					statusLabel.setText("");
					settingsPanel.setEnabledComponents(true);
					dryRunCheckBox.setEnabled(true);
					settingsPanel.setEnabledComponents(true);
					targetUrlTextField.setEditable(true);
					if(!error) {
						showAlert("Scan finished with no errors", false);
						// @TOOD switch to results Tab
					}
				}
			};

			if(dryRunCheckBox.isSelected()) {
				executor.runCrawler(events);
			} else {
				executor.runXSSScanner(events);
			}
		} else {
			stopScan();
		}
	}
	
	public void stopScan() {
		if(executor != null) {
			executor.requestStopScan();
			btnToggleScan.setEnabled(false);
		}
	}

	public void saveState() {
		if(burpApi == null)return;
		burpApi.persistence().preferences().setString("jsonConfig", settingsPanel.getJson());
		PersistedObject prjData = burpApi.persistence().extensionData();
		prjData.setString("target_url", targetUrlTextField.getText());
	}
	
	public void loadState() {
		if(burpApi == null)return;
		String j = burpApi.persistence().preferences().getString("jsonConfig");
		if(j == null)
			return;
		settingsPanel.loadJson(j);
		PersistedObject prjData = burpApi.persistence().extensionData();
		targetUrlTextField.setText(prjData.getString("target_url"));
		//settingsPanel.checkScannerIsConfigured();
	}

	public void showAlert(String message, boolean isError) {
		Component parent = burpApi != null ? burpApi.userInterface().swingUtils().suiteFrame() : DomdigUI.this;
		if(isError) {
			JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(parent, message);
		}
	}

	public DomdigUI(MontoyaApi burpApi) {
		this.burpApi = burpApi; 
		try{
			Class.forName("org.sqlite.JDBC");
		} catch(ClassNotFoundException e){}

		setLayout(new BorderLayout(0, 0));

		JPanel mainPanel = new JPanel();
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		mainPanel.add(topPanel, BorderLayout.NORTH);
		GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWidths = new int[]{0, 0};
		gbl_topPanel.rowHeights = new int[]{0, 0, 0};
		gbl_topPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_topPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		topPanel.setLayout(gbl_topPanel);

		JPanel panel = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel.getLayout();
		flowLayout_3.setHgap(10);
		flowLayout_3.setVgap(0);
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(20, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		topPanel.add(panel, gbc_panel);

		JLabel lblNewLabel = new JLabel("Target URL");
		panel.add(lblNewLabel);

		targetUrlTextField = new JTextField();
		panel.add(targetUrlTextField);
		targetUrlTextField.setColumns(40);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		panel.add(separator);

		btnToggleScan = new JButton("Start Scan");
		panel.add(btnToggleScan);
		btnToggleScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
  					public void run() { 
						startScan();
					}
				});
			}
		});
		btnToggleScan.setFont(new Font("Lucida Grande", Font.PLAIN, 14));

		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		panel.add(separator_1);

		statusLabel = new JLabel("");
		panel.add(statusLabel);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 12, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		topPanel.add(panel_1, gbc_panel_1);

		dryRunCheckBox = new JCheckBox("Just crawl the target, do not scan for XSS");
		panel_1.add(dryRunCheckBox);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JPanel scanTab = new JPanel();
		tabbedPane.addTab("Scan", null, scanTab, null);
		scanTab.setLayout(new BorderLayout(0, 0));


		JPanel crawlerTab = new JPanel();
		crawlResultsPanel = new CrawlResultsPanel(burpApi); 
		tabbedPane.addTab("Crawl Results", null, crawlerTab, null);
		crawlerTab.setLayout(new BorderLayout(0, 0));
		crawlerTab.add(crawlResultsPanel);


		JPanel vulnerabilitiesTab = new JPanel();
		tabbedPane.addTab("Vulnerabilities", null, vulnerabilitiesTab, null);
		vulnerabilitiesTab.setLayout(new BorderLayout(0, 0));


		vulnerabilitiesPanel = new VulnerabilitiesPanel(); 
		vulnerabilitiesTab.add(vulnerabilitiesPanel);

		settingsPanel = new SettingsPanel(this);
		scanTab.add(settingsPanel);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			   public void run() {
				   loadState();
			   }
		});
	}

}
