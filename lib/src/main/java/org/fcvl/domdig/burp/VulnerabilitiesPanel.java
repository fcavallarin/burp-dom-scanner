package org.fcvl.domdig.burp;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class VulnerabilitiesPanel extends JPanel {
	private JTable vulnerabilitieTable;
	private VulnerabilitiesTableModel vulnerabilitiesModel;
	private JLabel requestTriggerLabel;
	private JTextField urlTextField;
	private JTextField idTextField;
	private JTextField typeTextField;
	private JTextField descrTextField;
	private JTextField payloadTextField;
	private JTextField elementTextField;
	private JLabel confirmedLabel;
	

	public VulnerabilitiesPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
		
		JScrollPane requestsScrollPane = new JScrollPane();
		splitPane.setLeftComponent(requestsScrollPane);
		
		vulnerabilitiesModel = new VulnerabilitiesTableModel();
		vulnerabilitieTable = new JTable(vulnerabilitiesModel);
		vulnerabilitieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vulnerabilitieTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				if (!lse.getValueIsAdjusting()) {
					if(vulnerabilitieTable.getSelectedRow() > -1) {
						DomdigVulnerability r = ((VulnerabilitiesTableModel)vulnerabilitieTable.getModel()).getRow(vulnerabilitieTable.getSelectedRow());
						//vulnerabilityTextArea.setText(r.payload);
						urlTextField.setText(r.url);
						idTextField.setText(r.id+"");
						typeTextField.setText(r.type);
						descrTextField.setText(r.description);
						payloadTextField.setText(r.payload);
						elementTextField.setText(r.element);
						confirmedLabel.setText(r.confirmed ? "YES" : "NO");
					}
				}
			}
		});
		setColWidths(vulnerabilitieTable, new int[]{20, 50, 300, 200, 100, 50, 300});
		setTableSorter(vulnerabilitieTable);
		
		requestsScrollPane.setViewportView(vulnerabilitieTable);
		
		JPanel requestDetailsPanel = new JPanel();
		requestDetailsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel detailsPanel = new JPanel();
		detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		detailsPanel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		requestDetailsPanel.add(scrollPane, BorderLayout.CENTER);
		detailsPanel.add(requestDetailsPanel);
		splitPane.setRightComponent(detailsPanel);
		JPanel panel = new JPanel();
		panel.setBorder(null);
		scrollPane.setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("ID");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		idTextField = new JTextField();
		GridBagConstraints gbc_idTextField = new GridBagConstraints();
		gbc_idTextField.insets = new Insets(0, 0, 5, 0);
		gbc_idTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_idTextField.gridx = 1;
		gbc_idTextField.gridy = 0;
		panel.add(idTextField, gbc_idTextField);
		idTextField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Type");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		typeTextField = new JTextField();
		GridBagConstraints gbc_typeTextField = new GridBagConstraints();
		gbc_typeTextField.insets = new Insets(0, 0, 5, 0);
		gbc_typeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeTextField.gridx = 1;
		gbc_typeTextField.gridy = 1;
		panel.add(typeTextField, gbc_typeTextField);
		typeTextField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Description");
		lblNewLabel_2.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		descrTextField = new JTextField();
		GridBagConstraints gbc_descrTextField = new GridBagConstraints();
		gbc_descrTextField.insets = new Insets(0, 0, 5, 0);
		gbc_descrTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_descrTextField.gridx = 1;
		gbc_descrTextField.gridy = 2;
		panel.add(descrTextField, gbc_descrTextField);
		descrTextField.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Payload");
		lblNewLabel_3.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		panel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		payloadTextField = new JTextField();
		GridBagConstraints gbc_payloadTextField = new GridBagConstraints();
		gbc_payloadTextField.insets = new Insets(0, 0, 5, 0);
		gbc_payloadTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_payloadTextField.gridx = 1;
		gbc_payloadTextField.gridy = 3;
		panel.add(payloadTextField, gbc_payloadTextField);
		payloadTextField.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Element");
		lblNewLabel_4.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 4;
		panel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		elementTextField = new JTextField();
		GridBagConstraints gbc_elementTextField = new GridBagConstraints();
		gbc_elementTextField.insets = new Insets(0, 0, 5, 0);
		gbc_elementTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_elementTextField.gridx = 1;
		gbc_elementTextField.gridy = 4;
		panel.add(elementTextField, gbc_elementTextField);
		elementTextField.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Confirmed");
		lblNewLabel_5.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 5;
		panel.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		confirmedLabel = new JLabel("YES");
		GridBagConstraints gbc_confirmedLabel = new GridBagConstraints();
		gbc_confirmedLabel.anchor = GridBagConstraints.WEST;
		gbc_confirmedLabel.insets = new Insets(0, 0, 5, 0);
		gbc_confirmedLabel.gridx = 1;
		gbc_confirmedLabel.gridy = 5;
		panel.add(confirmedLabel, gbc_confirmedLabel);
		
		JLabel lblNewLabel_6 = new JLabel("URL");
		lblNewLabel_6.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 6;
		panel.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		urlTextField = new JTextField();
		GridBagConstraints gbc_urlTextField = new GridBagConstraints();
		gbc_urlTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_urlTextField.gridx = 1;
		gbc_urlTextField.gridy = 6;
		panel.add(urlTextField, gbc_urlTextField);
		urlTextField.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		requestDetailsPanel.add(panel_2, BorderLayout.NORTH);
		
		requestTriggerLabel = new JLabel("");
		panel_2.add(requestTriggerLabel);

	}
	
	public void setColWidths(JTable tbl, int[] colWidths){
		for(int i = 0; i < colWidths.length; i++){
			TableColumn col = tbl.getColumnModel().getColumn(i);
			if(col != null)
				col.setPreferredWidth(colWidths[i]);
		}

	}

	public void setTableSorter(JTable tbl) {
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tbl.getModel());
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		//sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
	}


	public void loadVulnerabilitiesList(ArrayList<DomdigVulnerability> list) {
		if(list != null) {
			for(DomdigVulnerability u: list) {
				vulnerabilitiesModel.addRow(u);

			}
		}
	}

	public void flushTable() {
		vulnerabilitiesModel.flush();
	}

	public void reset() {
		flushTable();
		//vulnerabilityTextArea.setText("");
		urlTextField.setText("");
		idTextField.setText("");
		typeTextField.setText("");
		descrTextField.setText("");
		payloadTextField.setText("");
		elementTextField.setText("");
		confirmedLabel.setText("");
	}
}


class VulnerabilitiesTableModel extends AbstractTableModel {
	private String[] columnNames = {"#", "Type", "Description", "Payload", "Element", "Confirmed", "URL"};
	private Class<?> colClasses[] = {Integer.class, String.class, String.class, String.class, String.class, String.class, String.class};
	public ArrayList<DomdigVulnerability> data = new ArrayList<>();
	private ArrayList<String> withIconOk = new ArrayList<>();

	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if(data == null) return 0;
		return data.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public void setIconOk(String value) {
		this.withIconOk.add(value);
		fireTableDataChanged();
	}
	
	public Object getValueAt(int row, int col) {
		
		DomdigVulnerability vuln = data.get(row);
		
		switch(col){
			case 0: return vuln.id;
			case 1: return vuln.type;
			case 2: return vuln.description;
			case 3: return vuln.payload;
			case 4: return vuln.element;
			case 5: return vuln.confirmed ? "YES" : "NO";
			case 6: return vuln.url;
		}

		return null;
	}


	public Class getColumnClass(int c) {
		return colClasses[c];	    
	}

	public boolean isCellEditable(int row, int col) {

		return false ;
	}

	public void setData(ArrayList<DomdigVulnerability> data){	    	
		this.data = data;
		fireTableDataChanged();
		//printDebugData();
	}
	
	public Boolean addRow(DomdigVulnerability data){	    

		this.data.add(data);
		fireTableDataChanged();
		//printDebugData();
		return true;
	}
	
	public void delRow(int i){	    	
		this.data.remove(i);
		fireTableDataChanged();
	}
	
	public DomdigVulnerability getRow(int index) {
		if(index == -1) {
			return null;
		}
		return this.data.get(index);
	}
	
	public void flush(){		
		this.data = new ArrayList<>();
		this.withIconOk = new ArrayList<>();
		fireTableDataChanged();
	}	
}


