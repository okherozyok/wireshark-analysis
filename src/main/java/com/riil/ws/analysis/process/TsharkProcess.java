package com.riil.ws.analysis.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riil.ws.analysis.buf.IAnalyzer;

@Service
public class TsharkProcess {
	final private Logger LOGGER = LoggerFactory.getLogger(TsharkProcess.class);
	final private String SCRIPTS_DIR = "scripts/";
	private final int PROCESS_WAIT_FOR = 3;
	private final File STDOUT = new File("stdout");
	private final File STDERR = new File("stderr");

	private String script = "tshark2json.bat";

	@Autowired
	private IAnalyzer analyzer;

	public void run() {
		try {
			timing((VOID) -> execScript(), "Exec script using ");
			timing((VOID) -> load2Mem(), "Load to mem using ");
			timing((VOID) -> analysis(), "Analysis using ");
			timing((VOID) -> output(), "Output using ");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			System.exit(0);
		}
	}

	private void execScript() throws Exception {
		Process p = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(SCRIPTS_DIR + script);

			pb.redirectOutput(Redirect.to(STDOUT));
			pb.redirectError(Redirect.to(STDERR));
			p = pb.start();
			p.waitFor(PROCESS_WAIT_FOR, TimeUnit.MINUTES);

		} finally {
			if (p != null) {
				p.destroy();
			}
		}

	}

	private void load2Mem() throws Exception {
		try (FileInputStream fis = new FileInputStream(STDOUT)) {
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String indexLine = null;
			while ((indexLine = br.readLine()) != null) {
				String frameLine = br.readLine();
				if (null == frameLine) {
					LOGGER.error("Not expected frame line : null");
					System.exit(1);
				}
				analyzer.save(indexLine, frameLine);
			}
		}
	}

	private void analysis() throws Exception {

		analyzer.analysis();
	}

	private void output() throws Exception {
		analyzer.output();
	}

	private <T> void timing(CosumerCheckedException<T> cosumer, String msg) throws Exception {
		long start = System.currentTimeMillis();
		cosumer.accept(null);
		long end = System.currentTimeMillis();
		LOGGER.info(msg + (end - start) + "ms");
	}
}
