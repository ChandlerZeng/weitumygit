package com.libtop.weituR.dao;

import android.content.Context;

import com.libtop.weituR.application.AppApplication;
import com.libtop.weituR.dao.bean.DaoSession;
import com.libtop.weituR.dao.bean.History;
import com.libtop.weituR.dao.bean.HistoryDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class HistoryBo {

	private HistoryDao historyDao;

	public HistoryBo(Context context) {
		DaoSession mDaoSession = AppApplication.getDaoSession(context);
		historyDao = mDaoSession.getHistoryDao();
	}

	public long save(History history) {
		return historyDao.insert(history);
	}

	public List<History> lists(String lid) {
		QueryBuilder<History> qb = historyDao.queryBuilder();
		List<History> list = qb.where(HistoryDao.Properties.Lid.eq(lid))
				.orderDesc(HistoryDao.Properties.Update_time).limit(50).list();
		return list;
	}

	public long saveUpdate(String name, String isbn, String url, String auth,
			String report, String lid,String publisher) {
		QueryBuilder<History> qb = historyDao.queryBuilder();
		qb.where(HistoryDao.Properties.Name.eq(name));
		List<History> list = qb.list();
		if (list.size() > 0) {
			History history = list.get(0);
			history.setUpdate_time(System.currentTimeMillis());
			historyDao.update(history);
			return history.getId();
		} else {
			History history = new History();
			history.setIsbn(isbn);
			history.setName(name);
			history.setUrl(url);
			history.setAuth(auth);
			history.setReport(report);
			history.setLid(lid);
			history.setPublisher(publisher);
			history.setUpdate_time(System.currentTimeMillis());
			return historyDao.insert(history);
		}
	}

	public void clear() {
		historyDao.deleteAll();
	}

}
