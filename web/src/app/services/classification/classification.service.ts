import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {AlertService} from '../alert/alert.service';
import {Classification} from '../../models/classification';
import {AlertModel, AlertType} from '../../models/alert';
import {saveAs} from 'file-saver/FileSaver';
import {TaskanaDate} from '../../shared/util/taskana.date';

@Injectable()
export class ClassificationService {

  url = environment.taskanaRestUrl + '/v1/classificationdefinitions';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient, private alertService: AlertService) {
  }

  // GET
  exportClassifications(domain: string) {
    domain = (domain === '' ? '' : '?domain=' + domain);
    this.httpClient.get<Classification[]>(this.url + domain, this.httpOptions)
      .subscribe(
        response => saveAs(new Blob([JSON.stringify(response)], {type: 'text/plain;charset=utf-8'}),
          'Classifications_' + TaskanaDate.getDate() + '.json')
      );
  }

  // POST
  // TODO handle error
  importClassifications(classifications: any) {
    this.httpClient.post(this.url + '/import',
      JSON.parse(classifications), this.httpOptions).subscribe(
      classificationsUpdated => this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Import was successful')),
      error => this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'Import was not successful'))
    );
  }
}
