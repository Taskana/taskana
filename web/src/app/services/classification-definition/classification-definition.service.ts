import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {AlertService} from '../alert/alert.service';
import {ClassificationDefinition} from '../../models/classification-definition';
import {AlertModel, AlertType} from '../../models/alert';
import {saveAs} from 'file-saver/FileSaver';
import {TaskanaDate} from '../../shared/util/taskana.date';
import {ErrorModel} from '../../models/modal-error';
import {ErrorModalService} from '../errorModal/error-modal.service';

@Injectable()
export class ClassificationDefinitionService {

  url = environment.taskanaRestUrl + '/v1/classificationdefinitions';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient, private alertService: AlertService,
              private errorModalService: ErrorModalService) {
  }

  // GET
  exportClassifications(domain: string) {
    domain = (domain === '' ? '' : '?domain=' + domain);
    this.httpClient.get<ClassificationDefinition[]>(this.url + domain, this.httpOptions)
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
      error => this.errorModalService.triggerError(new ErrorModel(
        `There was an error importing classifications`, error.message))
    );
  }
}
