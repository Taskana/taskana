import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {environment} from '../../environments/environment';
import {saveAs} from 'file-saver/FileSaver';
import {Classification} from '../model/classification';
import {DatePipe} from '@angular/common';
import {AlertModel, AlertService, AlertType} from './alert.service';

@Injectable()
export class ClassificationService {

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient, private alertService: AlertService) {
  }

  // GET
  getClassificationDomains(): Observable<string[]> {
    return this.httpClient.get<string[]>(environment.taskanaRestUrl + '/v1/classifications/domains', this.httpOptions);
  }

  // GET
  exportAllClassifications() {
    this.httpClient.get<Classification[]>(environment.taskanaRestUrl + '/v1/classificationdefinitions', this.httpOptions)
      .subscribe(
        response => saveAs(new Blob([JSON.stringify(response)], {type: 'text/plain;charset=utf-8'}), this.generateName())
      );
  }

  // GET
  exportClassificationsByDomain(domain: string) {
    this.httpClient.get<Classification[]>(environment.taskanaRestUrl + '/v1/classificationdefinitions?domain=' + domain, this.httpOptions)
      .subscribe(
        response => saveAs(new Blob([JSON.stringify(response)], {type: 'text/plain;charset=utf-8'}), this.generateName(domain))
      );
  }

  // POST
  // TODO handle error
  importClassifications(classifications: any) {
    console.log('importing classifications');
    this.httpClient.post(environment.taskanaRestUrl + '/v1/classificationdefinitions/import',
      JSON.parse(classifications), this.httpOptions).subscribe(
      classificationsUpdated => this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Import was successful')),
      error => this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'Import was not successful'))
    );
  }

  private generateName(domain = ''): string {
    const dateFormat = 'yyyy-MM-ddTHH:mm:ss';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);
    const date = datePipe.transform(Date.now(), dateFormat) + 'Z';
    return 'Classifications_' + date + '.json';
  }
}
