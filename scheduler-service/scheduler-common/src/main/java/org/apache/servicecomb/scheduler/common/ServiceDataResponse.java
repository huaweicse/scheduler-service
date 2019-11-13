/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.scheduler.common;

/**
 *  ServiceResponse with contents. When this invocation is successful, the result is set.Otherwise, the error is set.
 */
public class ServiceDataResponse<RESULT, ERROR> extends ServiceResponse {
  private RESULT result;

  private ERROR error;

  public RESULT getResult() {
    return result;
  }

  public void setResult(RESULT result) {
    this.result = result;
  }

  public ERROR getError() {
    return error;
  }

  public void setError(ERROR error) {
    this.error = error;
  }

  public static ServiceDataResponse newSuccessServiceResponse() {
    return new ServiceDataResponse<>();
  }
}
