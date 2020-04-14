import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { DomainService } from 'app/services/domain/domain.service';
import { TaskanaType } from 'app/models/taskana-type';
import { MessageModal } from 'app/models/message-modal';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { environment } from 'environments/environment';
import { AlertService } from 'app/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/models/alert';
import { UploadService } from 'app/shared/services/upload/upload.service';
import { ImportExportService } from 'app/administration/services/import-export/import-export.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ERROR_TYPES } from '../../../models/errors';
import { ErrorsService } from '../../../services/errors/errors.service';
import { ErrorModel } from '../../../models/error-model';

@Component({
  selector: 'taskana-import-export-component',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {
  @Input() currentSelection: TaskanaType;

  @ViewChild('selectedFile', { static: true })
  selectedFileInput;

  domains: string[] = [];
  errorWhileUploadingText: string;

  constructor(
    private domainService: DomainService,
    private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationDefinitionService: ClassificationDefinitionService,
    private generalModalService: GeneralModalService,
    private alertService: AlertService,
    public uploadservice: UploadService,
    private errorsService: ErrorsService,
    private importExportService: ImportExportService
  ) {
  }

  ngOnInit() {
    this.domainService.getDomains().subscribe(
      data => {
        this.domains = data;
      }
    );
  }

  export(domain = '') {
    if (this.currentSelection === TaskanaType.WORKBASKETS) {
      this.workbasketDefinitionService.exportWorkbaskets(domain);
    } else {
      this.classificationDefinitionService.exportClassifications(domain);
    }
  }

  uploadFile() {
    const file = this.selectedFileInput.nativeElement.files[0];
    const formdata = new FormData();
    const ajax = new XMLHttpRequest();
    if (this.checkFormatFile(file)) {
      formdata.append('file', file);
      ajax.upload.addEventListener('progress', this.progressHandler.bind(this), false);
      ajax.addEventListener('load', this.resetProgress.bind(this), false);
      ajax.addEventListener('error', this.onFailedResponse.bind(this, ajax), false);
      ajax.onreadystatechange = this.onReadyStateChangeHandler.bind(this, ajax);
      if (this.currentSelection === TaskanaType.WORKBASKETS) {
        ajax.open('POST', `${environment.taskanaRestUrl}/v1/workbasket-definitions`);
      } else {
        ajax.open('POST', `${environment.taskanaRestUrl}/v1/classification-definitions`);
      }
      if (!environment.production) {
        ajax.setRequestHeader('Authorization', 'Basic YWRtaW46YWRtaW4=');
      }
      ajax.send(formdata);
      this.uploadservice.isInUse = true;
      this.uploadservice.setCurrentProgressValue(1);
    }
  }

  progressHandler(event) {
    const percent = (event.loaded / event.total) * 100;
    this.uploadservice.setCurrentProgressValue(Math.round(percent));
  }

  private checkFormatFile(file): boolean {
    const ending = file.name.match(/\.([^.]+)$/)[1];
    let check = false;
    if (ending === 'json') {
      check = true;
    } else {
      file.value = '';
      this.errorsService.updateError(ERROR_TYPES.FILE_ERR);
    }
    return check;
  }

  private resetProgress() {
    this.uploadservice.setCurrentProgressValue(0);
    this.uploadservice.isInUse = false;
    this.selectedFileInput.nativeElement.value = '';
  }

  private onReadyStateChangeHandler(event) {
    if (event.readyState === 4 && event.status >= 400) {
      let title;
      let key: ERROR_TYPES;
      if (event.status === 401) {
        key = ERROR_TYPES.IMPORT_ERR_1;
        title = 'Import was not successful, you have no access to apply this operation.';
      } else if (event.status === 404) {
        key = ERROR_TYPES.IMPORT_ERR_2;
      } else if (event.status === 409) {
        key = ERROR_TYPES.IMPORT_ERR_3;
      } else if (event.status === 413) {
        key = ERROR_TYPES.IMPORT_ERR_4;
      }
      this.errorHandler(key, event);
    } else if (event.readyState === 4 && event.status === 200) {
      // new Key: ALERT_TYPES.SUCCESS_ALERT_6
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Import was successful'));
      this.importExportService.setImportingFinished(true);
      this.resetProgress();
    }
  }

  private onFailedResponse() {
    this.errorHandler(ERROR_TYPES.UPLOAD_ERR);
  }

  private errorHandler(key: ERROR_TYPES, passedError?: HttpErrorResponse) {
    this.errorsService.updateError(key, passedError);
    delete this.selectedFileInput.files;
    this.resetProgress();
  }
}
