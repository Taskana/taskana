import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { configureTests } from 'app/app.test.configuration';
import { NgxsModule, Store } from '@ngxs/store';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { of } from 'rxjs';
import { ACTION } from 'app/shared/models/action';
import { TreeNodeModel } from 'app/shared/models/tree-node';
import { Location } from '@angular/common';
import { UpdateClassification } from 'app/shared/store/classification-store/classification.actions';
import { TaskanaTreeComponent } from './tree.component';
import { ClassificationDefinition } from '../../models/classification-definition';
import { LinksClassification } from '../../models/links-classfication';
import { ClassificationsService } from '../../services/classifications/classifications.service';

@Component({
  selector: 'tree-root',
  template: ''
})
class TreeVendorComponent {
  @Input() options;
  @Input() state;
  @Input() nodes;
}

describe('TaskanaTreeComponent', () => {
  let component: TaskanaTreeComponent;
  let fixture: ComponentFixture<TaskanaTreeComponent>;
  let classificationsService;
  let moveNodeEvent;
  let dropEvent;

  const locationSpy: jasmine.SpyObj<Location> = jasmine.createSpyObj('Location', ['go', 'path']);
  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select', 'dispatch']);

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      imports: [AngularSvgIconModule, HttpClientModule, NgxsModule.forRoot()],
      declarations: [TreeVendorComponent],
      providers: [ClassificationsService,
        { provide: Location, useValue: locationSpy },
        { provide: Store, useValue: storeSpy }]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      locationSpy.path.and.callFake(() => '');
      storeSpy.select.and.callFake(selector => {
        switch (selector) {
          case ClassificationSelectors.selectedClassificationId:
            return of('id4');
          case ClassificationSelectors.activeAction:
            return of(ACTION.CREATE);
          case ClassificationSelectors.classifications:
            return of([new TreeNodeModel('id4')]);
          default:
            return of();
        }
      });

      fixture = testBed.createComponent(TaskanaTreeComponent);
      classificationsService = testBed.get(ClassificationsService);
      moveNodeEvent = {
        eventName: 'moveNode',
        node: { classificationId: 'id4', parentId: '', parentKey: '', _links: { self: { href: 'url' } } },
        to: { parent: { classificationId: 'id3', key: 'key3' } }
      };

      dropEvent = {
        event: { target: { tagName: 'TREE-VIEWPORT' } },
        element: { data: { classificationId: 'id3', parentId: 'id1', parentKey: 'key1' } }
      };

      component = fixture.componentInstance;
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be change the classification parent (onMoveNode)', async () => {
    const classification = new ClassificationDefinition('id4',
      'key4', '', '', 'MANUAL', 'DOMAIN_A', 'TASK', true, '019-04-10T10:23:34.985Z', '2019-04-10T10:23:34.985Z',
      'classification4', 'description', 1, 'level', '', '', '', '', '', '',
      '', '', '', new LinksClassification({ href: '' }, '', '', { href: '' }, { href: '' }, { href: '' }));

    // using parameter 'any' since getClassification is a private method
    spyOn<any>(component, 'getClassification').and.returnValue(classification);
    spyOn(component, 'switchTaskanaSpinner');

    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');

    await component.onMoveNode(moveNodeEvent);

    expect(classification.parentId).toEqual('id3');
    expect(classification.parentKey).toEqual('key3');
    expect(storeSpy.dispatch).toHaveBeenCalledWith(new UpdateClassification(classification));
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(true);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(false);
  });

  it('should be changed the parent classification to root node (onDrop)', async () => {
    const classification = new ClassificationDefinition('id3',
      'key3', 'id1', 'key1', 'MANUAL', 'DOMAIN_A', 'TASK', true, '019-04-10T10:23:34.985Z', '2019-04-10T10:23:34.985Z',
      'classification3', 'description', 1, 'level', '', '', '', '', '', '',
      '', '', '', new LinksClassification({ href: '' }, '', '', { href: '' }, { href: '' }, { href: '' }));

    // using parameter 'any' since getClassification is a private method
    spyOn<any>(component, 'getClassification').and.returnValue(classification);
    spyOn(component, 'switchTaskanaSpinner');

    expect(classification.parentId).toEqual('id1');
    expect(classification.parentKey).toEqual('key1');

    await component.onDrop(dropEvent);

    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(true);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(false);
  });
});
