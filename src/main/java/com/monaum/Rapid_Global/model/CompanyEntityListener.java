//package com.monaum.Rapid_Global.model;
//
///**
// * Monaum Hossain
// * @since jul 18, 2025
// */
//
//import com.monaum.Rapid_Global.module.master.company.Company;
//import com.monaum.Rapid_Global.security.CompanyContext;
//import jakarta.persistence.PrePersist;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CompanyEntityListener {
//
//    @PrePersist
//    public void setCompany(AbstractModel entity) {
//        if (entity.getCompany() != null) return;
//
//        Company activeCompany = CompanyContext.getActiveCompany();
//        if (activeCompany != null) {
//            entity.setCompany(activeCompany);
//        }
//    }
//}
