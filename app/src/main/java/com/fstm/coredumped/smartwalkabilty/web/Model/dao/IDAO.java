package com.fstm.coredumped.smartwalkabilty.web.Model.dao;

import java.util.Collection;

public interface IDAO<T>
{
    boolean Create(T obj);
    Collection<T> Retrieve();
    boolean delete(T obj);
}
