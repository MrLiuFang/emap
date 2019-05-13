//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class Main {
//
//	public static void main(String[] args) {
//		List<File> files = new ArrayList<File>();
//		files.add(new File("E:\\1355462237.jpg"));
//		files.add(new File("E:\\1417570536.jpg"));
//		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
//		String PREFIX = "--", LINE_END = "\r\n";
//		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
//		try {
//			URL httpUrl = new URL("http://47.106.176.89/file/add");
//			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
//			conn.setReadTimeout(5000);
//			conn.setConnectTimeout(5000);
//			conn.setDoInput(true); // 允许输入流
//			conn.setDoOutput(true); // 允许输出流
//			conn.setUseCaches(false); // 不允许使用缓存
//			conn.setRequestMethod("POST"); // 请求方式
//			conn.setRequestProperty("Charset", "UTF-8");
//			// 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
//			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//			// if(files.size()!= 0) {
//			/** * 当文件不为空，把文件包装并且上传 */
//			OutputStream outputSteam = conn.getOutputStream();
//			DataOutputStream dos = new DataOutputStream(outputSteam);
//			for (int i = 0; i < files.size(); i++) {
//				StringBuffer sb = new StringBuffer();
//				sb.append(PREFIX);
//				sb.append(BOUNDARY);
//				sb.append(LINE_END);
//				sb.append("Content-Disposition: form-data; name=\"img" + i + "\"; filename=\"" + files.get(i).getName()
//						+ "\"" + LINE_END);
//				// sb.append("Content-Type: application/octet-stream; charset="+ "UTF-8"
//				// +LINE_END);
//				sb.append(LINE_END);
//				dos.write(sb.toString().getBytes());
//				InputStream is = new FileInputStream(files.get(i));
//				byte[] bytes = new byte[1024];
//				int len = -1;
//				while ((len = is.read(bytes)) != -1) {
//					dos.write(bytes, 0, len);
//				}
//
//				dos.write(LINE_END.getBytes());
//				is.close();
//
//			}
//			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
//			dos.write(end_data);
//			dos.flush();
//			/**
//			 * 获取响应码 200=成功 当响应成功，获取响应的流
//			 */
//			int res = conn.getResponseCode();
//			InputStream inputStream = conn.getInputStream();
//            //获取响应
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null){
//                System.out.println(line);
//            }
//            reader.close();
//            //该干的都干完了,记得把连接断了
//            conn.disconnect();
//			// Log.e(TAG, "response code:"+res);
//			if (res == 200) {
////                    listener.onFinish(res);
//			}
//
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return;
//	}
//}
