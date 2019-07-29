import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.sauronsoftware.jave.AudioUtils;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class Main {

	public static void main(String[] args) throws IOException, JRException {
//		File source = new File("E:\\project\\emap\\emap-run","2.amr");
//		File target = new File("E:\\project\\emap\\emap-run","‪1.mp3");
//		AudioUtils.amrToMp3(source, target);
		
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode jsonNode = objectMapper.readTree("{\"helpId\":[\"1\",\"2\"]}");
//		System.out.println(jsonNode.get("helpId"));
		
		JRAbstractExporter exporter = new JRPdfExporter();
		Test test = new Test();
		test.setName("test");
		
		Map<String, Object> map=new HashMap<String, Object>();
        map.put("name", "Test"); 
        
		JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
		File reportFile = null;
        reportFile = new File("E:","report1.jasper");
//        JasperReport report =  JasperCompileManager.compileReport(reportFile.getPath());
        JasperPrint jasperPrint = JasperFillManager.fillReport(new FileInputStream(reportFile), map);
        OutputStream out = new FileOutputStream("C:\\Users\\Mr.Liu\\Desktop\\report1.pdf");

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
        exporter.exportReport();
        out.flush();
	}
	
	
	
}
