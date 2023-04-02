package org.fcvl.domdig.burp;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.json.JSONObject;

import burp.api.montoya.MontoyaApi;

import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import burp.api.montoya.http.message.HttpHeader;
import  burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.editor.HttpRequestEditor;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.FlowLayout;


public class CrawlResultsPanel extends JPanel {
	private MontoyaApi burpApi;
	
	private JTable requestsTable;
	private RequestsTableModel requestsModel;
	private JTextArea requestTextArea;
	private JLabel requestTriggerLabel;
	private JTextField elementTextField;
	HttpRequestEditor reqEditor;

	private HttpRequest domdigRequestToBurpRequest(DomdigRequest req) {
		if(burpApi == null) return null;
		HttpRequest burpReq =  null;
		
		burpReq = HttpRequest.httpRequestFromUrl(req.url);
		if(!req.method.toUpperCase().equals("GET")) {
			burpReq = burpReq.withMethod(req.method);
		}

		if(req.headers != null && !req.headers.equals("")) {
			JSONObject headers = new JSONObject(req.headers);
			for(String k: headers.keySet()) {
				burpReq = burpReq.withAddedHeader(HttpHeader.httpHeader(k, headers.getString(k)));
			}
		}
		
		if(req.data != null && !req.data.equals("")) {
			burpReq = burpReq.withBody(req.data);
		}
		
		return burpReq;
	}
	
	private void sendToRepeater(DomdigRequest req) {
		HttpRequest burpReq = domdigRequestToBurpRequest(req);
		if(burpReq == null) {
			return;
		}

		burpApi.repeater().sendToRepeater(burpReq);
	}

	private void sendToIntruder(DomdigRequest req) {
		HttpRequest burpReq = domdigRequestToBurpRequest(req);
		if(burpReq == null) {
			return;
		}

		burpApi.intruder().sendToIntruder(burpReq);
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
		sorter.setSortKeys(sortKeys);
	}


	public void loadRequestsList(ArrayList<DomdigRequest> list) {
		if(list != null) {
			for(DomdigRequest u: list) {
				requestsModel.addRow(u);

			}
		}
	}

	public void flushTable() {
		requestsModel.flush();
	}

	private void flushRequestEditor() {
		if(burpApi != null) {
			reqEditor.setRequest(null);
		} else {
			requestTextArea.setText("");
		}
	}

	public void reset() {
		flushTable();
		flushRequestEditor();
	}

	private void loadRequestTextArea() {
		if(requestsTable.getSelectedRow() > -1) {
			DomdigRequest r = ((RequestsTableModel)requestsTable.getModel()).getRow(requestsTable.getSelectedRow());
			try {
				String rawReq = r.getRaw();
				if(burpApi != null) {
					reqEditor.setRequest(domdigRequestToBurpRequest(r));
				} else {
					requestTextArea.setText(rawReq);
					requestTextArea.setCaretPosition(0);
				}
			} catch (MalformedURLException e1) {
				flushRequestEditor();
			}

			if(r.trigger != null) {
				requestTriggerLabel.setText("Request triggered by " + r.triggerEvent + "() on ");
				elementTextField.setText(r.triggerElement);
				elementTextField.setVisible(true);
			} else {
				requestTriggerLabel.setText("");
				elementTextField.setVisible(false);
			}
		}
	}

	public CrawlResultsPanel(MontoyaApi burpApi) {
		this.burpApi = burpApi;
		setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);

		JScrollPane requestsScrollPane = new JScrollPane();
		splitPane.setLeftComponent(requestsScrollPane);

		requestsModel = new RequestsTableModel();
		requestsTable = new JTable(requestsModel);
		requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requestsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				if (!lse.getValueIsAdjusting()) {
					loadRequestTextArea();
				}
			}
		});
		JPopupMenu popupMenu =  new JPopupMenu("a");
		JMenuItem m1 = new JMenuItem("Send to Repeater");
		m1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				DomdigRequest req = requestsModel.getRow(requestsTable.getSelectedRow());
				System.out.println(requestsTable.getSelectedRow());
				sendToRepeater(req);

			}
		});
		JMenuItem m2 = new JMenuItem("Send to Intruder");
		m2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				DomdigRequest req = requestsModel.getRow(requestsTable.getSelectedRow());
				sendToIntruder(req);

			}
		});
		popupMenu.add(m1);
		popupMenu.add(m2);
		requestsTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int r = requestsTable.rowAtPoint(e.getPoint());
					if (r >= 0 && r < requestsTable.getRowCount()) {
						requestsTable.setRowSelectionInterval(r, r);
						popupMenu.show(requestsTable, e.getX(), e.getY());
					} else {
						requestsTable.clearSelection();
					}
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		setColWidths(requestsTable, new int[]{50, 50, 70, 300, 200, 200});
		setTableSorter(requestsTable);

		requestsScrollPane.setViewportView(requestsTable);

		JPanel requestDetailsPanel = new JPanel();
		splitPane.setRightComponent(requestDetailsPanel);
		requestDetailsPanel.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		requestDetailsPanel.add(panel_2, BorderLayout.NORTH);

		requestTriggerLabel = new JLabel("");
		panel_2.add(requestTriggerLabel);
		
		elementTextField = new JTextField();
		panel_2.add(elementTextField);
		elementTextField.setColumns(70);

		if(burpApi != null) {
			reqEditor = burpApi.userInterface().createHttpRequestEditor();
			requestDetailsPanel.add(reqEditor.uiComponent(), BorderLayout.CENTER);
		} else {
			JScrollPane scrollPane = new JScrollPane();
			requestDetailsPanel.add(scrollPane, BorderLayout.CENTER);
			requestTextArea = new JTextArea();
			scrollPane.setViewportView(requestTextArea);
		}

	}

}



class RequestsTableModel extends AbstractTableModel {
	private String[] columnNames = {"#", "Type", "Method", "URL", "Data", "Trigger"};
	private Class<?> colClasses[] = {Integer.class, String.class, String.class, String.class, String.class, String.class};
	public ArrayList<DomdigRequest> data = new ArrayList<>();

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

	public Object getValueAt(int row, int col) {
		DomdigRequest req = data.get(row);

		switch(col){
			case 0: return req.id;
			case 1: return req.type;
			case 2: return req.method;
			case 3: return req.url;
			case 4: return req.data;
			case 5: 
				if(req.trigger != null) {
					return "$(" + req.triggerElement + ")." + req.triggerEvent + "()";
				} 
				return "";
		}
		return null;
	}


	public Class getColumnClass(int c) {
		return colClasses[c];	    
	}

	public boolean isCellEditable(int row, int col) {
		return false ;
	}

	public void setData(ArrayList<DomdigRequest> data){	    	
		this.data = data;
		fireTableDataChanged();
		//printDebugData();
	}

	public Boolean addRow(DomdigRequest data){	    
		this.data.add(data);
		fireTableDataChanged();
		return true;
	}

	public void delRow(int i){	    	
		this.data.remove(i);
		fireTableDataChanged();
	}

	public DomdigRequest getRow(int index) {
		if(index == -1) {
			return null;
		}
		return this.data.get(index);
	}

	public void flush(){		
		this.data = new ArrayList<>();
		fireTableDataChanged();
	}
}

