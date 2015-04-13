package server;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaHandler extends Handler {
	private JTextArea textArea = new JTextArea();
	private SimpleFormatter sf = new SimpleFormatter();
	
	public TextAreaHandler() {
		textArea.setEditable(false);
	}
	public void publish(LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textArea.append(sf.format(record));
			}

		});
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	public JTextArea getTextArea() {
		return this.textArea;
	}

}
