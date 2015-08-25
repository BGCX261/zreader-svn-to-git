package com.bookclub.epub;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.bookmodel.TOCTree.Reference;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.core.image.ZLImageData;
import org.geometerplus.zlibrary.core.image.ZLImageManager;
import org.geometerplus.zlibrary.text.model.ZLImageEntry;
import org.geometerplus.zlibrary.text.model.ZLTextParagraph;
import org.geometerplus.zlibrary.text.model.ZLTextParagraph.EntryIterator;
import org.geometerplus.zlibrary.text.view.ZLTextImageElement;
import org.geometerplus.zlibrary.text.view.ZLTextParagraphCursor;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageData;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;

import android.graphics.Bitmap;

public class MyEpub {

	public BookModel bookModel;
	
	public MyEpub(String epubFile)
	{
		Book book = createBookForFile(ZLFile.createFileByPath(epubFile));
		openBookInternal(book);
		openBookText(bookModel.TOCTree.subTrees().get(14));
	}	
	
	public ZLTextParagraphCursor OpenBookText2(TOCTree tocTree)
	{
		Reference ref = tocTree.getReference();
		return ZLTextParagraphCursor.cursor(bookModel.BookTextModel, ref.ParagraphIndex);
		
	}
	
	public void openBookText(TOCTree tocTree)
	{
		int paragraphIndex = tocTree.getReference().ParagraphIndex;
		for(int pi = paragraphIndex; pi<paragraphIndex+100;pi++)
		{
			ZLTextParagraph zlTextParagraph = bookModel.BookTextModel.getParagraph(pi);
			EntryIterator it = zlTextParagraph.iterator();
			while (it.hasNext()) {
				it.next();
				byte type = it.getType();
				switch (type)
				{
				case ZLTextParagraph.Entry.TEXT:
					char[] textData = it.getTextData();				
					int textOffset = it.getTextOffset();
					int textLength = it.getTextLength();
					String str = String.copyValueOf(textData, textOffset, textLength);
					int x=2;
					x++;
					break;
				case ZLTextParagraph.Entry.IMAGE:
					ZLImageEntry imageEntry = it.getImageEntry();
					final ZLImage image = imageEntry.getImage();
					if (image != null) {
						ZLAndroidImageData data = null;
						final ZLAndroidImageManager mgr = (ZLAndroidImageManager) ZLAndroidImageManager.Instance();
						data = mgr.getImageData(image);
						//获取图片、及宽度高度
						Bitmap bm = data.getBitmap();
						int h = bm.getHeight();
						int w = bm.getWidth();
						
						
						h=bm.getHeight();
					}
					break;
				case ZLTextParagraph.Entry.CONTROL:
				case ZLTextParagraph.Entry.FORCED_CONTROL:
				case ZLTextParagraph.Entry.FIXED_HSPACE:
					break;
					default:
						break;
				
				}		
				
			} 
		}
		
	}

	private void openBookInternal(Book book) {
		if (book != null) {
			bookModel = null;
			System.gc();
			System.gc();
			bookModel = BookModel.createModel(book);
			int test=0;
			test=2;
		}
	}
	private Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = Book.getByFile(file);
		if (book != null) {
			//book.insertIntoBookList();
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = Book.getByFile(child);
				if (book != null) {
					//book.insertIntoBookList();
					return book;
				}
			}
		}
		return null;
	}

	
}
