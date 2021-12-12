/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.dbCore.repository;

import org.palading.clivia.dbCore.domain.TblClientSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author palading_cr
 * @title CliviaTokenConfigRepository
 * @project clivia
 */
@Repository
public interface CliviaClientSecurityConfigRepository extends JpaRepository<TblClientSecurity, String> {

    @Query(value = "select count(1) from tbl_client_security where  token=?1 and state=0", nativeQuery = true)
    public int selectToken(String token);

    @Query(value = "select token from tbl_client_security where client_name=?1 and client_pwd=?2 and state=0 limit 1", nativeQuery = true)
        String getToken(String name, String pwd);

    @Query(value = "select state from tbl_client_security where token=?1 limit 1", nativeQuery = true)
    String selectClientState(String token);
}
