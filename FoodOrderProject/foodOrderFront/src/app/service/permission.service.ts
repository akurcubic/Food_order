import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../enviroments/environment.development";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(private http: HttpClient) { }

  getAuthHeadres() : HttpHeaders{
    return new HttpHeaders({
      Authorization: `Bearer ${localStorage.getItem('token')}`
    })
  }

  getAllPermissions(): Observable<Permissions[]>{

    return this.http.get<Permissions[]>(environment.permissionService, {headers: this.getAuthHeadres()});
  }
}
