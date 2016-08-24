package com.libtop.weituR.dao;

import android.content.Context;

import com.libtop.weituR.application.AppApplication;
import com.libtop.weituR.dao.bean.Book;
import com.libtop.weituR.dao.bean.BookDao;
import com.libtop.weituR.dao.bean.DaoSession;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class BookBo {
	private BookDao bookDao;

	private static BookBo bo;

	public static BookBo getInstance(Context context) {
		if (bo == null) {
			bo = new BookBo(context);
		}
		return bo;
	}

	public BookBo(Context context) {
		DaoSession mDaoSession = AppApplication.getDaoSession(context);
		bookDao = mDaoSession.getBookDao();
	}

	public long save(Book book) {
		return bookDao.insert(book);
	}
	
	public void saveUpdate(Book book) {
		QueryBuilder<Book> qb = bookDao.queryBuilder();
		List<Book> list = qb.where(BookDao.Properties.Isbn.eq(book.getIsbn())).list();
		if(list.size() == 0){
			bookDao.insert(book);
		}else{
			Book bookTemp = list.get(0);
			if(book.getFavorate()){
				bookTemp.setFavorate(true);
			}
			if(book.getWant_see()){
				bookTemp.setWant_see(true);
			}
			if(book.getSee_pre()){
				bookTemp.setSee_pre(true);
			}
			bookDao.update(bookTemp);
		}
	}

	public List<Book> listFavorate() {
		QueryBuilder<Book> qb = bookDao.queryBuilder();
		List<Book> list = qb.where(BookDao.Properties.Favorate.eq(true)).list();
		return list;
	}

	public List<Book> listWant() {
		QueryBuilder<Book> qb = bookDao.queryBuilder();
		List<Book> list = qb.where(BookDao.Properties.Want_see.eq(true)).list();
		return list;
	}

	public List<Book> listSee() {
		QueryBuilder<Book> qb = bookDao.queryBuilder();
		List<Book> list = qb.where(BookDao.Properties.See_pre.eq(true)).list();
		return list;
	}
}
