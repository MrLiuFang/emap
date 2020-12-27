package com.pepper.controller.emap.front.report;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.pdf.*;

import java.io.IOException;

public class EmapPdfPageEventHelper extends PdfPageEventHelper {
    // 模板
    public PdfTemplate tpl ;

    private String fontPath;

    private String username;

    public EmapPdfPageEventHelper(String fontPath, String username) {
        this.fontPath = fontPath;
        this.username = username;
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {

        try {
            //  初始化模板，模板的宽和高自己设定， 初始化一个字体，这个其实可以用 代码块实现
            tpl = writer.getDirectContent().createTemplate(100, 130);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        float center = document.getPageSize().getRight() / 2;//页面的水平中点
        float bottom = document.getPageSize().getBottom() + 20;
        //在每页结束的时候把“第x页”信息写道模版指定位置
        PdfContentByte cb = writer.getDirectContent();
//				// 线的宽度
//				cb.setLineWidth(1f);
//				// 线的起点，坐标这个是以左下角为原点的
//				cb.moveTo(70, 760);
//				// 线的终点
//				cb.lineTo(550, 760);
//				// stroke一下
//				cb.stroke();
//				cb.moveTo(70, 40);
//				cb.lineTo(550, 40);
//				cb.stroke();
//        cb.saveState();  saveState只能一次，不然会报错
        // 获得当前页
        String text = writer.getPageNumber() + "/";
        cb.beginText();
        try {
            cb.setFontAndSize(BaseFont.createFont(fontPath+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 8);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cb.setTextMatrix(center, bottom);//定位“第x页,共” 在具体的页面调试时候需要更改这xy的坐标
        cb.showText(text);
        cb.endText();
        cb.addTemplate(tpl, center + 8, bottom);//定位“y页” 在具体的页面调试时候需要更改这xy的坐标
        cb.stroke();
        cb.saveState();
        cb.restoreState();
        cb.closePath();
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        //关闭document的时候获取总页数，并把总页数按模版写道之前预留的位置
        tpl.beginText();
        try {
            tpl.setFontAndSize(BaseFont.createFont(fontPath+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 8);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tpl.showText(Integer.toString(writer.getPageNumber())+"         工作人员:"+username);
        tpl.endText();
        tpl.closePath();//sanityCheck();
    }
}
