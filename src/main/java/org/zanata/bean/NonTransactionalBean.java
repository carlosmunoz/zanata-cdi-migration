/*
 * Copyright 2015, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.bean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.zanata.transaction.TransactionUtil;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Named("nonTransactionalBean")
public class NonTransactionalBean {

    @Inject
    EntityManager em;

    public void nonTransactionalMethod() throws Exception {
        System.out.println("Should NOT be running in a Tx:" + em.getTransaction().isActive());

        TransactionUtil.runInTransaction(() -> {
            System.out.println("shoult be running in a transaction: "
                    + em.getTransaction().isActive());
        });
    }
}
