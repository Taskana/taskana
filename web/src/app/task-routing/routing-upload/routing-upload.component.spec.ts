import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoutingUploadComponent } from './routing-upload.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StartupService } from '../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../shared/services/window/window.service';
import { MatDialogModule } from '@angular/material/dialog';
import { DragAndDropDirective } from '../../shared/directives/drag-and-drop.directive';

describe('RoutingUploadComponent', () => {
  let component: RoutingUploadComponent;
  let fixture: ComponentFixture<RoutingUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RoutingUploadComponent, DragAndDropDirective],
      imports: [HttpClientTestingModule, MatDialogModule],
      providers: [StartupService, TaskanaEngineService, WindowRefService]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RoutingUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
