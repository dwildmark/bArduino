package server;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
/**
 * Loghandler that logs into a text area.
 * 
 * @author Olle Casperson
 *
 */
public class TextAreaHandler extends Handler {
	private JTextArea textArea = new JTextArea();
	private SimpleFormatter sf = new SimpleFormatter();
	
	public TextAreaHandler() {
		textArea.setEditable(false);
	}
	/**
	 * publish the log record
	 * @param record log record to publish
	 */
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
