package com.libtop.weitu.test;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/9.
 */
public class SubjectResource implements Serializable {
    public int status;
    public List<Subject> subjects;
    public List<Resource> resources;
    public Subject subject;
}
