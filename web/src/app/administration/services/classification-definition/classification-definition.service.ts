import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'app/../environments/environment';
import { AlertService } from 'app/services/alert/alert.service';
import { ClassificationDefinition } from 'app/models/classification-definition';
import { AlertModel, AlertType } from 'app/models/alert';
import { saveAs } from 'file-saver/FileSaver';
import { TaskanaDate } from 'app/shared/util/taskana.date';

@Injectable()
export class ClassificationDefinitionService {

  url = environment.taskanaRestUrl + '/v1/classificationdefinitions';
  constructor(
    private httpClient: HttpClient,
    private alertService: AlertService
  ) {
  }

  // GET
  exportClassifications(domain: string) {
    domain = (domain === '' ? '' : '?domain=' + domain);
    this.httpClient.get<ClassificationDefinition[]>(this.url + domain)
      .subscribe(
        response => saveAs(new Blob([JSON.stringify(response)], { type: 'text/plain;charset=utf-8' }),
          'Classifications_' + TaskanaDate.getDate() + '.json')
      );
  }

  // POST
  // TODO handle error
  importClassifications(classifications: any) {
    this.httpClient.post(this.url + '/import',
      JSON.parse(classifications)).subscribe(
        classificationsUpdated => this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Import was successful')),
        error => this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'Import was not successful'))
      );
  }
}
