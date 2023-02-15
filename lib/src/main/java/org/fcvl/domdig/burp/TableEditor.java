package org.fcvl.domdig.burp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TableEditor extends JPanel{
	private JTable keyValueTable;
	private String[] columns = null;
	private Integer[] columnWidths = null;
	public Boolean enabled = true;
	private JButton btnRemove;
	private JButton btnAdd;
	

	public ArrayList<String[]> getData() {
		ArrayList<String[]> ret = new ArrayList<>();
		for(Vector<?> v: ((DefaultTableModel)keyValueTable.getModel()).getDataVector()) {
			String[] row = new String[columns.length];
			for(int i = 0; i < columns.length; i++) {
				Object e = v.elementAt(i);
				row[i] = e != null ? e.toString() : "";
			}
			ret.add(row);
		}

		return ret;

	}
	
	public void setEnabled(Boolean enabled) {
		btnAdd.setEnabled(enabled);
		btnRemove.setEnabled(enabled);
		this.enabled = enabled;
	}
	
	public String toJson() {
		ArrayList<String[]> kvArr = getData();
		if(kvArr.size() == 0)
			return "[]";
		JSONArray jsonArr = new JSONArray();
		for(String[] v : kvArr) {
			JSONObject json = new JSONObject();
			for(int i = 0; i < columns.length; i++) {
				if(i < v.length && v[i] != null && !v[i].equals("")) {
					json.put(columns[i], v[i]);
				}
				
			}
			jsonArr.put(json);
		}
		return jsonArr.toString();
	}

	public void fromJson(String j) {
		JSONArray json = new JSONArray(j);
		for(Object k1 : json) {
			String[] row = new String[columns.length];
			for(int i = 0; i < columns.length; i++) {
				try {
					row[i] = ((JSONObject)k1).getString(columns[i]);
				} catch(JSONException je) {
					row[i] = "";
				}
			}
			addRow(row);
		}
	}

	public void addRow(String[] row) {
		if(!enabled)return;
		TableModel dt = (TableModel) keyValueTable.getModel();
		DefaultTableModel dt1 = (DefaultTableModel)dt; 
		dt1.addRow(row);		
	}

	public TableEditor(String[] columns, Integer[] columnWidths) {
		this.columns = columns;
		this.columnWidths = columnWidths;
		
		setLayout(new BorderLayout(0, 0));

		JPanel bottomPanel = new JPanel();
		FlowLayout fl_bottomPanel = (FlowLayout) bottomPanel.getLayout();
		fl_bottomPanel.setAlignment(FlowLayout.RIGHT);
		add(bottomPanel, BorderLayout.SOUTH);

		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!enabled)return;
				int i = keyValueTable.getSelectedRow();
				if(i > -1) {
					((DefaultTableModel)keyValueTable.getModel()).removeRow(i);
				}
			}
		});
		bottomPanel.add(btnRemove);

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(new String[] {"name", "value", "", ""});
			}
		});
		bottomPanel.add(btnAdd);

		JScrollPane keyValueTableScrollPane = new JScrollPane();
		add(keyValueTableScrollPane, BorderLayout.NORTH);
	
		Object[] headers = new Object[columns.length];
		for(int i = 0; i < columns.length; i++) {
			headers[i] = columns[i].substring(0, 1).toUpperCase() + columns[i].substring(1);
		}
		
		keyValueTable = new JTable(new DefaultTableModel(headers, 0));
		keyValueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		keyValueTableScrollPane.setViewportView(keyValueTable);

		keyValueTableScrollPane.setPreferredSize(new Dimension(700, 110));
		for(int i = 0; i < columnWidths.length; i++) {
			keyValueTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);	
		}
	
	}
}
