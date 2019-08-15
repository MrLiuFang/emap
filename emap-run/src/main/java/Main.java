//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.pdf.BaseFont;
//
//import it.sauronsoftware.jave.AudioUtils;
//import net.sf.jasperreports.engine.DefaultJasperReportsContext;
//import net.sf.jasperreports.engine.JRAbstractExporter;
//import net.sf.jasperreports.engine.JRDataSource;
//import net.sf.jasperreports.engine.JREmptyDataSource;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRExporterParameter;
//import net.sf.jasperreports.engine.JRPropertiesHolder;
//import net.sf.jasperreports.engine.JRPropertiesMap;
//import net.sf.jasperreports.engine.JRPropertiesUtil;
//import net.sf.jasperreports.engine.JRRuntimeException;
//import net.sf.jasperreports.engine.JasperCompileManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.JasperReportsContext;
//import net.sf.jasperreports.engine.JasperRunManager;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import net.sf.jasperreports.engine.export.JRPdfExporter;
//import net.sf.jasperreports.export.SimpleWriterExporterOutput;
//
//public class Main {
//
//	public static void main(String[] args) throws IOException, JRException, DocumentException {
//////		File source = new File("E:\\project\\emap\\emap-run","2.amr");
//////		File target = new File("E:\\project\\emap\\emap-run","‪1.mp3");
//////		AudioUtils.amrToMp3(source, target);
////		
//////		ObjectMapper objectMapper = new ObjectMapper();
//////		JsonNode jsonNode = objectMapper.readTree("{\"helpId\":[\"1\",\"2\"]}");
//////		System.out.println(jsonNode.get("helpId"));
//////		JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
//////		jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "Helvetica"); 
//////		jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
//////		jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "false");
////		JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
////		JRPropertiesUtil jrPropertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
//////		jrPropertiesUtil.setProperty("net.sf.jasperreports.default.pdf.font.name", "STSong-Light"); 
//////		jrPropertiesUtil.setProperty("net.sf.jasperreports.default.pdf.encoding", "UniCNS-UCS2-H");
//////		jrPropertiesUtil.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
//////		
//////		jrPropertiesUtil.setProperty("net.sf.jasperreports.default.pdf.font.name", "Helvetica"); 
//////		jrPropertiesUtil.setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
//////		jrPropertiesUtil.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
////		
////		Test test = new Test();
////		test.setEventDate("testtesttesttesttesttesttesttest");
////		Test test1 = new Test();
////		test1.setEventDate("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
////		Test test2 = new Test();
////		test2.setEventDate("test");
////		Test test3 = new Test();
////		test3.setEventDate("繁體");
////		
////		List<Test> mlist = new ArrayList<Test>();
////		mlist.add(test);
////		mlist.add(test1);
////		mlist.add(test2);
////		mlist.add(test3);
////
////		Map<String, Object> parameters=new HashMap<String, Object>();
////		parameters.put("title", "测试"); 
////        
//////		JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
//////
////////        JasperReport report =  JasperCompileManager.compileReport(reportFile.getPath());
//////        JasperPrint jasperPrint = JasperFillManager.fillReport(new FileInputStream(reportFile), map);
//////        OutputStream out = new FileOutputStream("C:\\Users\\Mr.Liu\\Desktop\\report1.pdf");
//////
//////        exporter.setExporterOutput(new SimpleWriterExporterOutput(reportFile));
////////        exporter.setConfiguration();
//////        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//////        exporter.exportReport();
//////        out.flush();
////        
////        File reportFile = new File("E:","eventDetailed.jasper");
////		if (!reportFile.exists()) {
////			throw new JRRuntimeException("FileWebappReport.jasper not found. The report design must be compiledfirst.");
////		}
////			
////		FileInputStream isRef = new FileInputStream(reportFile);
////		OutputStream out = new FileOutputStream("C:\\Users\\Mr.Liu\\Desktop\\eventDetailed.pdf");
////		JasperRunManager.runReportToPdfStream(isRef, out, parameters,new JRBeanCollectionDataSource(mlist));
////		out.flush();
////		out.close();
//		
//		System.out.println("1111.00112".replaceAll("(\\.(\\d*))", ""));
//
//	}
//	
//	
//	
//}
