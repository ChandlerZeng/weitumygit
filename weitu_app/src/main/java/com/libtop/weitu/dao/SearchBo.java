package com.libtop.weitu.dao;

import android.content.Context;

import com.libtop.weitu.application.AppApplication;
import com.libtop.weitu.dao.bean.DaoSession;
import com.libtop.weitu.dao.bean.Search;
import com.libtop.weitu.dao.bean.SearchDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class SearchBo
{

    private SearchDao searchDao;

    private static SearchBo bo;


    public static SearchBo getInstance(Context context)
    {
        if (bo == null)
        {
            bo = new SearchBo(context);
        }
        return bo;
    }


    public SearchBo(Context context)
    {
        DaoSession mDaoSession = AppApplication.getDaoSession(context);
        searchDao = mDaoSession.getSearchDao();
    }


    public long save(Search search)
    {
        return searchDao.insert(search);
    }


    public List<Search> lists()
    {
        QueryBuilder<Search> qb = searchDao.queryBuilder();
        List<Search> list = qb.orderDesc(SearchDao.Properties.Update_time).limit(50).list();
        return list;
    }


    public long sageUpdate(String name)
    {
        QueryBuilder<Search> qb = searchDao.queryBuilder();
        qb.where(SearchDao.Properties.Name.eq(name));
        List<Search> list = qb.list();
        if (list.size() > 0)
        {
            Search search = list.get(0);
            search.setUpdate_time(System.currentTimeMillis());
            searchDao.update(search);
            return search.getId();
        }
        else
        {
            Search search = new Search();
            search.setName(name);
            search.setUpdate_time(System.currentTimeMillis());
            return searchDao.insert(search);
        }
    }


    public void clear()
    {
        searchDao.deleteAll();
    }


    public void delete(Search search)
    {
        searchDao.delete(search);
    }


}
