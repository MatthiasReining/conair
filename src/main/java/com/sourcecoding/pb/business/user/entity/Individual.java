/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.user.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Individual.findByNickname, query = "SELECT i FROM Individual i WHERE i.nickname= :"+ Individual.findByNickname_Param_nickname),
    @NamedQuery(name = Individual.findAll, query = "SELECT i FROM Individual i")
})
public class Individual implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String findByNickname = "Individual#findByNickname";
    public static final String findByNickname_Param_nickname = "nickname";
    
    public static final String findAll = "Individual#findAll";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String nickname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    

 
    
}
