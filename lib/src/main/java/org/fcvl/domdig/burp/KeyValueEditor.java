package org.fcvl.domdig.burp;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.json.JSONObject;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class KeyValueEditor extends TableEditor{

	@Override
	public String toJson() {
		ArrayList<String[]> kvArr = getData();
		if(kvArr.size() == 0)
			return "{}";
		JSONObject json = new JSONObject();;
		for(String[] v : kvArr) {
			json.put(v[0], v[1]);
		}
		return json.toString();
	}

	@Override
	public void fromJson(String j) {
		JSONObject json = new JSONObject(j);
		for(String k : json.keySet()) {
			addRow(new String[] {k, json.getString(k)});
		}
	}
	
	public KeyValueEditor() {
		super(new String[] {"key", "value"}, new Integer[] {200, 600});
	}

}
