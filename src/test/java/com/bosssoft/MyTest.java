package com.bosssoft;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;

import org.junit.Test;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.WordUtils;
import com.bosssoft.platform.apidocs.enumtype.WordTitleType;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;
import com.bosssoft.platform.common.utils.FileUtils;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

public class MyTest {
   
	@Test
	public void test1(){
		File javaFile=new File("D:/github/bosssoft-apidocs/src/test/java/com/bosssoft/UserService.java");
		String serviceName=Utils.getJavaFileName(javaFile);
    	ClassOrInterfaceDeclaration declaration=ParseUtils.compilationUnit(javaFile).getClassByName(serviceName);
	
		List<MethodDeclaration> implementMethodDeclarationList=getMethodDeclaration(declaration);
		for (MethodDeclaration methodDeclaration : implementMethodDeclarationList) {
	              String type=getBasicType(methodDeclaration.getType());	
	              System.out.println(type);
	              
	              File f=null;
	              try{
	            	   f=  ParseUtils.searchJavaFile(javaFile, type);
	              }catch (Exception e) {
				    System.out.println(type+" can not to file java file");
				  }
	             if(f!=null){
	            	 ResponseNode responseNode = new ResponseNode();
		             ParseUtils.parseResponseNode(f, responseNode);
		             System.out.println(responseNode.toJsonApi()); 
	             }
	           
	             
	              
		}
			
			
	}
	
	@Test
    public void testWordUtil()throws Exception{
		WordUtils wordUtils=new WordUtils();
		String docTitle="测试";
		String path="d:\\test.doc";
		wordUtils.initDocument(path, docTitle);
        Document doc=wordUtils.document;
       /* Document document=new Document();
		PdfWriter.getInstance(document, new FileOutputStream("d:\\test.pdf"));
        document.open();*/
        
        /*Paragraph pUrl = new Paragraph();
		Anchor link = new Anchor("www.baidu.com");
		link.setReference("#service");
		pUrl.add(link);
		
		
		doc.add(pUrl);
		doc.newPage();
		
        wordUtils.renderTitle("1.1 第一张", WordTitleType.TITLE_1,"第一张");
        
        doc.newPage();
        
        Paragraph context = new Paragraph("");  */
        //设置行距    
       /* context.setLeading(3f);  
       context.setSpacingBefore(5);  
        context.setFirstLineIndent(20);
        Anchor contextlink = new Anchor("service");
        contextlink.setReference("#1");
       
        context.add(contextlink);
        
        doc.add(context);*/    
        
        
        /*context.setLeading(3f);  
        context.setSpacingBefore(5);  
         context.setFirstLineIndent(20);
        context.add(new Chunk("service")); 
        doc.add(context);
        doc.newPage();*/
        
   
       
        
		
		wordUtils.endDoc();
    }
	
	
	
	
	//@Test
	public void testDoc() throws Exception{
		Document document = new Document(PageSize.A5.rotate()); 
		String path="d:\\test.doc";
		
		if(!new File(path).exists()) FileUtils.mkdir(new File(path), false);
		
		RtfWriter2.getInstance(document, new FileOutputStream("d:\\test.doc")); 
		document.open(); 
		Font titleFont = new Font(Font.NORMAL,16, Font.BOLD); 
			//设置标题1格式	 
		RtfParagraphStyle rtfGsBt1 = RtfParagraphStyle.STYLE_HEADING_1; 
		rtfGsBt1.setAlignment(Element.ALIGN_CENTER); 
		rtfGsBt1.setStyle(Font.BOLD); 
		rtfGsBt1.setSize(14); 
			//设置标题2格式	 
		RtfParagraphStyle rtfGsBt2 = RtfParagraphStyle.STYLE_HEADING_2; 
		rtfGsBt2.setAlignment(Element.ALIGN_LEFT); 
		rtfGsBt2.setStyle(Font.NORMAL); 
		rtfGsBt2.setSize(12); 
		
		//Paragraph title = new Paragraph("测试"); 
		/*title.setAlignment(Element.ALIGN_CENTER); 
		title.setFont(titleFont); */
		//document.add(title); 
		
		//document.newPage();
		//正文 
		/*Paragraph title = new Paragraph("第一章"); 
		title.setFont(rtfGsBt1); 
		document.add(title); 
		title = new Paragraph("1.1 第一节"); 
		title.setFont(rtfGsBt2); 
		document.add(title); 
		title = new Paragraph("1.2 第二节"); 
		title.setFont(rtfGsBt2); 
		document.add(title); 
		title = new Paragraph("第二章"); 
		title.setFont(rtfGsBt1); 
		document.add(title); 
		title = new Paragraph("2.1 第一节"); 
		title.setFont(rtfGsBt2); 
		document.add(title); 
		title = new Paragraph("2.2 第二节"); 
		title.setFont(rtfGsBt2); 
		document.add(title); */
		
		
	
        
 
        /** 正文字体 author:yyli Sep 15, 2010 */
        RtfFont contextFont = new RtfFont("仿宋_GB2312", 9, Font.NORMAL,
                Color.BLACK);

        
        
        /*Cell cell=null;
        cell=new Cell(new com.lowagie.text.Paragraph("姓名", contextFont));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(cell);
        
       
        cell=new Cell(new com.lowagie.text.Paragraph("性别", contextFont));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(cell);
       
       
        cell=new Cell(new com.lowagie.text.Paragraph("身份证号", contextFont));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(cell);
       
       
        cell=new Cell(new com.lowagie.text.Paragraph("我", contextFont));
        table.addCell(cell);
        
        cell=new Cell(new com.lowagie.text.Paragraph("女", contextFont));
        table.addCell(cell);
        
        cell=new Cell(new com.lowagie.text.Paragraph("000000000014", contextFont));
        table.addCell(cell);
        
        String[] columns={"姓名","年龄","出生日期"};*/
        
        String[] columns={"姓名","年龄","出生日期"};
        
   
        Table aTable =new Table(3);
		for(String s:columns) {
		//把表格上方的标题创建出来
		aTable.addCell(new Cell(s));
		}
		//把数据填写到表格中，只要够了表格数量会自动换行
		for(int i=0;i<=2;i++){
		aTable.addCell(new Cell("ddd"));
		aTable.addCell(new Cell("12"));
		aTable.addCell(new Cell("11"));
		
		}
        
        document.add(aTable);
        document.close();
       
       /* WordUtils utils=new WordUtils();
        utils.initDocument("d:\\test.doc", "测试");
        utils.renderContent("ddddd");
		utils.endDoc();*/
	}
	
    private String getBasicType(Type type) {
    	String typeStr=type.asString();
    	if(ParseUtils.TYPE_MODEL.equals(typeStr)||"Void".equalsIgnoreCase(typeStr)){
    		return type.asString();
    	}else if(type instanceof ClassOrInterfaceType) {
    		ClassOrInterfaceType cot=(ClassOrInterfaceType) type;
    		String name=cot.getNameAsString();
    		if(name.equals("Map")||name.equals("HashMap")||name.equals("TreeMap")||name.equals("Hashtable")){
    			NodeList<Type> list=cot.getTypeArguments();
    			if(list.size()>0){
    				return getBasicType(list.get(1));
    			}
    		}else if(name.equals("List")||name.equals("LinkedList")||name.equals("ArrayList")||name.equals("Vector")||name.equals("Set")){
    			NodeList<Type> list=cot.getTypeArguments();
    			if(list.size()>0){
    				return getBasicType(list.get(0));
    			}
    		}
    	}
   
    	return type.asString();
	}

	private List<MethodDeclaration> getMethodDeclaration(ClassOrInterfaceDeclaration c) {
		
		List<MethodDeclaration> result=new ArrayList<>();
		List<MethodDeclaration> list=c.getChildNodesByType(MethodDeclaration.class);
		
		for (MethodDeclaration m : list) {
			if(m.getModifiers().contains(Modifier.PUBLIC)){
				result.add(m);
			}
		}
		return result;
	}
	
	
	
}
