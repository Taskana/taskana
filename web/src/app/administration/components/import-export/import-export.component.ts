import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaType } from 'app/shared/models/taskana-type';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { Observable, Subject } from 'rxjs';
import { HotToastService } from '@ngneat/hot-toast';
import { takeUntil } from 'rxjs/operators';

/**
 * Recommendation: Turn this component into presentational component - no logic, instead events are
 * fired back to parent components with @Output(). This way the logic of exporting/importing workbasket
 * or classification is stored in their respective container component.
 */
@Component({
  selector: 'taskana-administration-import-export',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit, OnDestroy {
  @Input() currentSelection: TaskanaType;
  @Input() parentComponent: string;

  @ViewChild('selectedFile', { static: true })
  selectedFileInput;

  domains$: Observable<string[]>;
  destroy$ = new Subject<void>();

  constructor(
    private domainService: DomainService,
    private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationDefinitionService: ClassificationDefinitionService,
    private notificationService: NotificationService,
    private importExportService: ImportExportService,
    private hotToastService: HotToastService
  ) {}

  ngOnInit() {
    this.domains$ = this.domainService.getDomains();
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
    if (this.checkFormatFile(file)) {
      if (this.currentSelection === TaskanaType.WORKBASKETS) {
        this.workbasketDefinitionService
          .importWorkbasket(file)
          .pipe(
            takeUntil(this.destroy$),
            this.hotToastService.observe({
              loading: 'Uploading...',
              success: 'File successfully uploaded',
              error: 'Upload failed'
            })
          )
          .subscribe({
            next: () => {
              this.importExportService.setImportingFinished(true);
            }
          });
      } else {
        this.classificationDefinitionService
          .importClassification(file)
          .pipe(
            takeUntil(this.destroy$),
            this.hotToastService.observe({
              loading: 'Uploading...',
              success: 'File successfully uploaded',
              error: 'Upload failed'
            })
          )
          .subscribe({
            next: () => {
              this.importExportService.setImportingFinished(true);
            }
          });
      }
    }
    this.resetProgress();
  }

  private checkFormatFile(file): boolean {
    const ending = file.name.match(/\.([^.]+)$/)[1];
    let check = false;
    if (ending === 'json') {
      check = true;
    } else {
      file.value = '';
      this.notificationService.showError('IMPORT_EXPORT_UPLOAD_FILE_FORMAT');
    }
    return check;
  }

  private resetProgress() {
    this.selectedFileInput.nativeElement.value = '';
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
