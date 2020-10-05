import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { ImportExportComponent } from './import-export.component';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { WorkbasketDefinitionService } from '../../services/workbasket-definition.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { UploadService } from '../../../shared/services/upload/upload.service';
import { ImportExportService } from '../../services/import-export.service';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { ClassificationDefinitionService } from '../../services/classification-definition.service';
import { take } from 'rxjs/operators';
import { TaskanaType } from '../../../shared/models/taskana-type';
import { BlobGenerator } from '../../../shared/util/blob-generator';

jest.mock('../../../shared/util/blob-generator');

describe('ImportExportComponent', () => {
  let fixture: ComponentFixture<ImportExportComponent>;
  let debugElement: DebugElement;
  let app: ImportExportComponent;

  const domainServiceSpy = jest.fn().mockImplementation(
    (): Partial<DomainService> => ({
      getSelectedDomainValue: jest.fn().mockReturnValue(of()),
      getSelectedDomain: jest.fn().mockReturnValue(of()),
      getDomains: jest.fn().mockReturnValue(of())
    })
  );

  const httpSpy = jest.fn().mockImplementation(
    (): Partial<HttpClient> => ({
      get: jest.fn().mockReturnValue(of([])),
      post: jest.fn().mockReturnValue(of([]))
    })
  );

  const showDialogFn = jest.fn().mockReturnValue(true);
  const notificationServiceSpy = jest.fn().mockImplementation(
    (): Partial<NotificationService> => ({
      showDialog: showDialogFn,
      showToast: showDialogFn,
      triggerError: showDialogFn
    })
  );

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [ImportExportComponent],
      providers: [
        StartupService,
        TaskanaEngineService,
        WindowRefService,
        WorkbasketDefinitionService,
        ClassificationDefinitionService,
        UploadService,
        ImportExportService,
        { provide: DomainService, useClass: domainServiceSpy },
        { provide: NotificationService, useClass: notificationServiceSpy },
        { provide: HttpClient, useClass: httpSpy }
      ]
    }).compileComponents();

    jest.clearAllMocks();

    fixture = TestBed.createComponent(ImportExportComponent);
    debugElement = fixture.debugElement;
    app = fixture.debugElement.componentInstance;
    app.currentSelection = TaskanaType.WORKBASKETS;
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(app).toBeTruthy();
  });

  it('should successfully upload a valid file', () => {
    app.selectedFileInput = {
      nativeElement: {
        files: [
          {
            lastModified: 1599117374674,
            name: 'Workbaskets_2020-09-03T09_16_14.1414Z.json',
            size: 59368,
            type: 'application/json',
            webkitRelativePath: ''
          }
        ]
      }
    };
    app.uploadFile();
    expect(app.uploadService.isInUse).toBeTruthy();
  });

  it('should trigger an error when uploading an invalid file format', () => {
    app.selectedFileInput = {
      nativeElement: {
        files: [
          {
            lastModified: 1599117374674,
            name: 'Workbaskets_2020-09-03T09_16_14.1414Z.pdf',
            size: 59368,
            type: 'application/pdf',
            webkitRelativePath: ''
          }
        ]
      }
    };
    app.uploadFile();
    expect(notificationServiceSpy).toHaveBeenCalled();
  });

  it('should successfully export the workbaskets', async (done) => {
    app
      .export()
      .pipe(take(1))
      .subscribe(() => {
        expect(BlobGenerator.saveFile).toHaveBeenCalledWith([], expect.stringMatching(/Workbaskets_.*\.json/));
        done();
      });
  });

  it('should successfully export the classifications', async (done) => {
    app.currentSelection = TaskanaType.CLASSIFICATIONS;
    app
      .export()
      .pipe(take(1))
      .subscribe(() => {
        expect(BlobGenerator.saveFile).toHaveBeenCalledWith([], expect.stringMatching(/Classifications_.*\.json/));
        done();
      });
  });
});
