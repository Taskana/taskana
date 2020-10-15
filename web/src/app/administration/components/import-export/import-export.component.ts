import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaType } from 'app/shared/models/taskana-type';
import { environment } from 'environments/environment';
import { UploadService } from 'app/shared/services/upload/upload.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

@Component({
  selector: 'taskana-administration-import-export',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {
  @Input() currentSelection: TaskanaType;
  @Input() parentComponent: string;

  @ViewChild('selectedFile', { static: true })
  selectedFileInput;

  domains: string[] = [];
  errorWhileUploadingText: string;

  constructor(
    private domainService: DomainService,
    private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationDefinitionService: ClassificationDefinitionService,
    private notificationsService: NotificationService,
    public uploadService: UploadService,
    private errorsService: NotificationService,
    private importExportService: ImportExportService
  ) {}

  ngOnInit() {
    this.domainService.getDomains().subscribe((data) => {
      this.domains = data;
    });
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
      this.uploadService.isInUse = true;
      this.uploadService.setCurrentProgressValue(1);
    }
  }

  progressHandler(event) {
    const percent = (event.loaded / event.total) * 100;
    this.uploadService.setCurrentProgressValue(Math.round(percent));
  }

  private checkFormatFile(file): boolean {
    const ending = file.name.match(/\.([^.]+)$/)[1];
    let check = false;
    if (ending === 'json') {
      check = true;
    } else {
      file.value = '';
      this.errorsService.triggerError(NOTIFICATION_TYPES.FILE_ERR);
    }
    return check;
  }

  private resetProgress() {
    this.uploadService.setCurrentProgressValue(0);
    this.uploadService.isInUse = false;
    this.selectedFileInput.nativeElement.value = '';
  }

  private onReadyStateChangeHandler(event) {
    if (event.readyState === 4 && event.status >= 400) {
      let title;
      let key: NOTIFICATION_TYPES;
      if (event.status === 401) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_1;
        title = 'Import was not successful, you have no access to apply this operation.';
      } else if (event.status === 404) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_2;
      } else if (event.status === 409) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_3;
      } else if (event.status === 413) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_4;
      }
      this.errorHandler(key, event);
    } else if (event.readyState === 4 && event.status === 200) {
      this.notificationsService.showToast(NOTIFICATION_TYPES.SUCCESS_ALERT_6);
      this.importExportService.setImportingFinished(true);
      this.resetProgress();
    }
  }

  private onFailedResponse() {
    this.errorHandler(NOTIFICATION_TYPES.UPLOAD_ERR);
  }

  private errorHandler(key: NOTIFICATION_TYPES, passedError?: HttpErrorResponse) {
    this.errorsService.triggerError(key, passedError);
    delete this.selectedFileInput.files;
    this.resetProgress();
  }
}
