package com.kyu.process;

/**
 * @FileName : Main.java
 * @Project : sample_project
 * @Date : 2012. 4. 27.
 * @작성자 : 이남규
 * @프로그램설명 :
 */
public class Main {

	/**
	 * <pre>
	 * main
	 *
	 * <pre>
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.test();
	}

	/**
	 * <pre>
	 * test
	 *
	 * <pre>
	 */
	public void test() {
		ProcessHandler handler = new ProcessHandler();
		ProcessVO processVO = new ProcessVO();
		processVO.setExecProgram("Ping");
		processVO.setParam("yahoo.co.kr");

		processVO = handler.job(processVO);
		if (processVO.isSuccess()) {
			System.out.println("##test success## isSuccess=" + processVO.isSuccess());
		}

	}
}
