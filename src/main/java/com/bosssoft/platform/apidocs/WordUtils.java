package com.bosssoft.platform.apidocs;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.bosssoft.platform.apidocs.parser.mate.AttributeNode;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.bosssoft.platform.common.utils.FileUtils;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.document.RtfDocumentSettings;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

public class WordUtils {
	
	public  Document document;
	
	private Font titleFont;
	private Font thFont;
	private Font tdFont;
	private Font tableTitleFont;
	private RtfParagraphStyle rtfGsBt1;//一级标题
	private RtfParagraphStyle rtfGsBt2;
	private RtfParagraphStyle rtfGsBt3;
	private RtfParagraphStyle rtfGsBt4;
	private RtfFont contextFont;
	private RtfParagraphStyle heading_4=new RtfParagraphStyle("heading 4","Normal");
    
	/**
	 * 初始化word文档
	 * @param path
	 * @param docTitle
	 * @return
	 * @throws Exception
	 */
	public  void initDocument(String path,String docTitle) throws Exception{
		
		
		Document document = new Document(PageSize.A5.rotate()); 
        if(!new File(path).exists()) FileUtils.mkdir(new File(path), false);
		
        RtfWriter2 writer =RtfWriter2.getInstance(document, new FileOutputStream(path)); 
		document.open(); 
		
		//构造风格
		constuctStyle(path);
		
		//注册4级标题
		RtfDocumentSettings settings = writer.getDocumentSettings();
        settings.registerParagraphStyle(heading_4);
       
		Paragraph title = new Paragraph(docTitle); 
		title.setAlignment(Element.ALIGN_CENTER); 
		title.setFont(titleFont); 
		document.add(title); 
		document.newPage();
		this.document=document;
	}
	
	private void constuctStyle(String path) throws Exception {
		titleFont = new Font(Font.NORMAL,16, Font.BOLD);
		//一级标题
		rtfGsBt1=RtfParagraphStyle.STYLE_HEADING_1;
		rtfGsBt1.setAlignment(Element.ALIGN_CENTER); 
		rtfGsBt1.setStyle(Font.BOLD); 
		rtfGsBt1.setSize(14); 
		rtfGsBt1.setSpacingBefore(10);
		
		//二级标题
		rtfGsBt2=RtfParagraphStyle.STYLE_HEADING_2;
		rtfGsBt2.setAlignment(Element.ALIGN_LEFT); 
		rtfGsBt2.setStyle(Font.BOLD); 
		rtfGsBt2.setSize(12);
		rtfGsBt2.setSpacingBefore(10);
		//三级标题
		rtfGsBt3=RtfParagraphStyle.STYLE_HEADING_3;
		rtfGsBt3.setAlignment(Element.ALIGN_LEFT); 
		rtfGsBt3.setStyle(Font.BOLD); 
		rtfGsBt3.setSize(11); 
		rtfGsBt3.setSpacingBefore(8);
		
		rtfGsBt4 = heading_4;
		rtfGsBt4.setAlignment(Element.ALIGN_LEFT);
		rtfGsBt4.setStyle(Font.BOLD);
		rtfGsBt4.setSize(10);
		rtfGsBt4.setSpacingBefore(8);
		
		//正文
		contextFont=new RtfFont("宋 体", 9, Font.NORMAL,Color.BLACK);
		
		//表头字体
		//thFont=new Font(Font.NORMAL,10, Font.BOLD);
		thFont= FontFactory.getFont("宋体", 9);
		//表格字体
		tdFont=FontFactory.getFont("宋体", 9);
		
		//表头字体
		tableTitleFont=FontFactory.getFont("楷体_gb2312", 10);
	}

	public void renderTitle(String titleName,WordTitleType titleType) throws DocumentException{
		Paragraph title = new Paragraph(titleName); 
		title.setSpacingBefore(50);
		switch (titleType){
		case TITLE_1:
			title.setFont(rtfGsBt1);
			break;
		case TITLE_2:
			title.setFont(rtfGsBt2);
			break;
		case TITLE_3:
			title.setFont(rtfGsBt3);
			break;
	    default:
	    	title.setFont(rtfGsBt4);
			break;
		}
		document.add(title);
	
	}
	
	public void  renderParamTableHashRequired(List<ParamNode> list)throws Exception{
		 String[] columns={"参数名","类型","必须","描述"};
		 renderParamTable(list,columns,true);
		 
	}
	
	
	public void  renderParamTableHash(List<ParamNode> list)throws Exception{
		 String[] columns={"参数名","类型","描述"};
		  renderParamTable(list,columns,false);
	}
	
	private  void  renderParamTable(List<ParamNode> list,String[] columns,Boolean hashRequired) throws Exception{
		 Table table=new Table(columns.length);
		 for (String string : columns) {
			// table.addCell(new Cell(string));
			 table.addCell(getTableThCell(string));
		}
		 
		 //填充数据
		 for (ParamNode paramNode : list) {
			 table.addCell(getTableTdCell(paramNode.name));
			 table.addCell(getTableTdCell(paramNode.type));
			 if(hashRequired) table.addCell(getTableTdCell(String.valueOf(paramNode.required)));
			 table.addCell(getTableTdCell(paramNode.description));
		}
		 table.setOffset(1f);
		 document.add(table);
	}
	
	private Cell getTableThCell(String string) {
		//Cell cell=new Cell(string);
		Cell cell=new Cell();
		cell.add(new Phrase(string, thFont));
		 cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		return cell;
	}
	
	private Cell getTableTdCell(String string) {
		//Cell cell=new Cell(string);
		Cell cell=new Cell();
		cell.add(new Phrase(string, tdFont));
		 cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		
		return cell;
	}

	public void  renderExceptionTable(List<Explain> throwsNode) throws Exception{
		 String[] columns={"异常类型","描述"};
		 Table table=new Table(columns.length);
		 for (String string : columns) {
			 table.addCell(getTableThCell(string));
		}
		 
		 //填充数据
		 for (Explain explain : throwsNode) {
			 table.addCell(getTableThCell(explain.getType()));
			 table.addCell(getTableThCell(explain.getDescription()));
		}
		 table.setOffset(1f);
		 document.add(table);
	}
	
	public void renderEntityTable(List<AttributeNode> attributeList) throws Exception{
		 String[] columns={"属性名","属性类型","表字段","描述"};
		 Table table=new Table(columns.length);
		 for (String string : columns) {
			 table.addCell(getTableTdCell(string));
		}
		 
		 //填充数据
		 for (AttributeNode attributeNode : attributeList) {
			 table.addCell(getTableThCell(attributeNode.getAttributeName()));
			 table.addCell(getTableThCell(attributeNode.getAttributeType()));
			 table.addCell(getTableThCell(attributeNode.getColumnName()));
			 table.addCell(getTableThCell(attributeNode.getDescription()));
		}
		table.setOffset(1f);
		 document.add(table);
	}
	
	
	
	public void renderContent(String content) throws Exception{
		
        Paragraph context = new Paragraph(content);    
        //设置行距    
        context.setLeading(3f);  
        // 正文格式左对齐    
      //  context.setAlignment(elementAlign);  
        context.setFont(contextFont);    
        // 离上一段落（标题）空的行数    
       context.setSpacingBefore(5);  
        // 设置第一行空的列数    
        context.setFirstLineIndent(20);
        
        document.add(context);    
	}
	
	public void endDoc(){
		this.document.close();
	}
	
	public void  renderTableTitle(String content)throws Exception{
		Paragraph context = new Paragraph(content);    
        //设置行距    
        context.setLeading(3f);  
        // 正文格式左对齐    
      //  context.setAlignment(elementAlign);  
        context.setFont(tableTitleFont);    
        // 离上一段落（标题）空的行数    
       context.setSpacingBefore(1);  
        // 设置第一行空的列数    
        context.setFirstLineIndent(20);
        context.setAlignment(Element.ALIGN_CENTER);
        document.add(context); 
	}
	
	public void renderListTitle(String context,WordListType type) throws Exception{
		com.lowagie.text.List subList=new com.lowagie.text.List(false, false, 10);
	
		switch (type){
		case DIAMOND:
			subList.setListSymbol("\u2666");
			break;
		case SPOTS:
			subList.setListSymbol("\u2022");
			subList.setIndentationLeft(35);
			break;
		default:
		}
		ListItem item=new ListItem(context);
		item.setSpacingBefore(6);
        subList.add(item);
        document.add(subList); 
	}
	
	
	public void renderList(List<Explain> list)throws Exception{
		com.lowagie.text.List subList=new com.lowagie.text.List(false, false, 10);
	        subList.setListSymbol("\u2022");
	        for (Explain explain : list) {
	        	ListItem item=new ListItem(explain.getDescription());
	        	item.setSpacingBefore(4);
	        	subList.add(item);
	        }
	        subList.setIndentationLeft(20);
	        document.add(subList);
	}
	
	public void renderJsonStr(String str)throws Exception{
		Table table=new Table(1);
		Cell cell=new Cell();
		cell.add(new Phrase(str, thFont));
		table.addCell(cell);
		table.setOffset(1f);
		document.add(table);
	}
}
