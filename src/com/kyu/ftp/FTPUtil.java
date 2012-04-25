package com.kyu.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.oroinc.net.ftp.FTP;
import com.oroinc.net.ftp.FTPClient;
import com.oroinc.net.ftp.FTPReply;

/**
 * @FileName : FtpUtil.java
 * @Project : sample_project
 * @Date : 2012. 4. 16.
 * @작성자 : 이남규
 * @프로그램설명 :
 */
public class FTPUtil {

	/** The client. */
	private FTPClient client = null;

	/**
	 *  생성자
	 */
	public FTPUtil() {
		client = new FTPClient();
	}

	/**
	 * <pre>
	 * connect
	 * FTP 접속
	 * <pre>
	 * @param remoteIp
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public boolean connect(String remoteIp, int port) throws Exception {
		boolean isConnected = false;
		try {
			client = new FTPClient();
			client.connect(remoteIp, port);

			// 연결 성공 응답 코드 확인
            int reply = client.getReplyCode();
            isConnected = FTPReply.isPositiveCompletion(reply);
            if (isConnected == false) {
            	client.disconnect();
            	System.out.println("##connect failed## remoteIp=" + remoteIp + ", port=" + port);
            }

		} catch (IOException ex) {
			if(client.isConnected()) {
                try {
                	client.disconnect();
                } catch(IOException f) {}
            }

			System.out.println("##connect exception## remoteIp=" + remoteIp + ", port=" + port);
			ex.printStackTrace();
			throw ex;
		}

		return isConnected;
	}

	/**
	 * <pre>
	 * cd
	 * 디렉토리 이동
	 * <pre>
	 * @param path
	 * @throws IOException
	 */
	public void cd(String path) throws IOException {
		client.changeWorkingDirectory(path);
	}

	/**
	 * <pre>
	 * login
	 * 로그인
	 * <pre>
	 * @param userId
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public boolean login(String userId, String password) throws IOException {
		boolean isFtpLogin = client.login(userId, password);
		client.enterLocalPassiveMode();
		client.setFileType(FTP.BINARY_FILE_TYPE);
		return isFtpLogin;
	}

	/**
	 * <pre>
	 * logOut
	 * 로그 아웃
	 * <pre>
	 * @throws IOException
	 */
	public void logout() throws IOException  {
		client.logout();
	}

	/**
	 * <pre>
	 * disconnect
	 * 접속 끓기
	 * <pre>
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		client.disconnect();
	}

	/**
	 * <pre>
	 * deleteFile
	 * 파일 삭제
	 * <pre>
	 * @param file
	 * @return
	 */
	public boolean deleteFile(File file) {
		boolean fileDelFlag = false;
		if (file.exists()) {
			fileDelFlag = file.delete();
		}
		return fileDelFlag;
	}

	/**
	 * <pre>
	 * getRetrieveFile
	 * FTP 파일 다운로드
	 * <pre>
	 * @param localFilePath
	 * @param remoteFileName
	 * @return
	 * @throws Exception
	 */
	public boolean getRetrieveFile(String downloadLocalDirectory, List<String> downloadFileNameList, String downloadRemoteDirectory) throws Exception {
		boolean downFlag = false;
		FileOutputStream fos = null;

		try {
			cd(downloadRemoteDirectory); // 디렉토리 이동

			for (String downloadFileName : downloadFileNameList) {
				String downloadLocalFilePath = makeFilePath(downloadLocalDirectory, downloadFileName); // 다운로드 받을 로컬 path

				File file = new File(downloadLocalFilePath);
				fos = new FileOutputStream(file);

				downFlag = client.retrieveFile(downloadFileName, fos);
				if (downFlag) {
					System.out.println("##getRetrieveFile success## downloadRemoteDirectory=" + downloadRemoteDirectory + ", downloadFileName=" + downloadFileName + ", downloadLocalDirectory=" + downloadLocalDirectory);
				} else {
					boolean isDelete = deleteFile(file); // 파일 다운로드 실패 시 로컬에 생성한 파일 삭제
					System.out.println("##getRetrieveFile failed## downloadRemoteDirectory=" + downloadRemoteDirectory + ", downloadFileName=" + downloadFileName + ", downloadLocalDirectory=" + downloadLocalDirectory + ", isDelete=" + isDelete);
				}
			}

		} catch (Exception ex) {
			System.out.println("##getRetrieveFile exception## downloadRemoteDirectory=" + downloadRemoteDirectory + ", downloadFileNameList=" + downloadFileNameList + ", downloadLocalDirectory=" + downloadLocalDirectory);
			ex.printStackTrace();
			throw ex;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {}
			}
		}

		return downFlag;
	}

	/**
	 * <pre>
	 * fileUpload
	 * 파일 업로드
	 * <pre>
	 * @param uploadLocalFilePath
	 * @param uploadFileName
	 */
	public void uploadFile(String uploadLocalDirectory, List<String> uploadFileNameList, String uploadRemoteDirectory) throws Exception {
		InputStream in = null;
		try {
			cd(uploadRemoteDirectory); // 디렉토리 이동
			client.setFileType(FTP.BINARY_FILE_TYPE);

			for (String uploadFileName : uploadFileNameList) {
				String uploadLocalFilePath = makeFilePath(uploadLocalDirectory, uploadFileName);
				in = new FileInputStream(uploadLocalFilePath); // 로컬 파일 read

				if (client.storeFile(uploadFileName, in)) {
					System.out.println("##uploadFile success## remoteDirectory=" + uploadRemoteDirectory + ", uploadLocalDirectory=" + uploadLocalDirectory + ", uploadFileName=" + uploadFileName);
				} else {
					System.out.println("##uploadFile failed## remoteDirectory=" + uploadRemoteDirectory + ", uploadLocalDirectory=" + uploadLocalDirectory + ", uploadFileName=" + uploadFileName);
				}
			}

		} catch (Exception ex) {
			System.out.println("##uploadFile exception## remoteDirectory=" + uploadRemoteDirectory + ", uploadLocalDirectory=" + uploadLocalDirectory + ", uploadFileNameList=" + uploadFileNameList);
			ex.printStackTrace();
			throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {}
			}
		}
	}

	/**
	 * <pre>
	 * makeFilePath
	 *
	 * <pre>
	 * @param directory
	 * @param fileName
	 * @return
	 */
	public String makeFilePath(String directory, String fileName) {
		StringBuilder path = new StringBuilder();
		path.append(directory);
		path.append(File.separator);
		path.append(fileName);
		return path.toString();
	}
}