import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from "./http-request.service";

export interface NationalityData {
  nationalities: string[];
}

@Injectable({
  providedIn: 'root'
})
export class NationalityService {

  constructor(private requestService: HttpRequestService) { }

  getNationalities(): Observable<any> {
    return this.requestService.get('/nationalities');
  }
}
