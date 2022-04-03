package com.fstm.coredumped.smartwalkabilty.core.danger.bo;

import java.util.HashSet;
import java.util.Set;

public abstract class Danger
{
    protected int id;
    protected int degree;
    public abstract double CalculateRisk();
    protected Set<Declaration> declarations=new HashSet<>();
    public Danger() {
    }

    public Set<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(Set<Declaration> declarations) {
        this.declarations = declarations;
    }

    public Danger(int id, int degree) {
        this.id = id;
        this.degree = degree;
    }

    public Danger(int degree) {
        this.degree = degree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
