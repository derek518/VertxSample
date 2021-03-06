/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


/**
 A service interface manage entities.
 <p>
 This service is an event bus service (aka. service proxy).
 </p>

 @class
*/
export default class SoldierService {

  constructor (eb: any, address: string);

  initializePersistence(resultHandler: (err: any, result: any) => any) : SoldierService;

  addOne(item: Object, principal: Object, resultHandler: (err: any, result: Object) => any) : SoldierService;

  retrieveOne(id: string, principal: Object, resultHandler: (err: any, result: Object) => any) : SoldierService;

  retrieveAll(principal: Object, resultHandler: (err: any, result: Array<Object>) => any) : SoldierService;

  count(condition: Object, principal: Object, resultHandler: (err: any, result: any) => any) : SoldierService;

  retrieveManyByCondition(condition: Object, principal: Object, resultHandler: (err: any, result: Array<Object>) => any) : SoldierService;

  updateOne(id: string, item: Object, principal: Object, resultHandler: (err: any, result: Object) => any) : SoldierService;

  deleteOne(id: string, principal: Object, resultHandler: (err: any, result: any) => any) : SoldierService;

  change(id: string, item: Object, principal: Object, resultHandler: (err: any, result: Object) => any) : SoldierService;

  retrieveArchives(cardId: string, principal: Object, resultHandler: (err: any, result: Array<Object>) => any) : SoldierService;
}