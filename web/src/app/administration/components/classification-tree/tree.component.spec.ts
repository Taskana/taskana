import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { TreeService } from 'app/administration/services/tree.service';
import { configureTests } from 'app/app.test.configuration';
import { NgxsModule } from '@ngxs/store';
import { ClassificationTreeComponent } from './tree.component';
import { ClassificationDefinition } from '../../../models/classification-definition';
import { LinksClassification } from '../../../models/links-classfication';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';

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
  let component: ClassificationTreeComponent;
  let fixture: ComponentFixture<ClassificationTreeComponent>;
  let classificationsService;
  let moveNodeEvent;
  let dropEvent;

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      imports: [AngularSvgIconModule, HttpClientModule, NgxsModule.forRoot()],
      declarations: [TreeVendorComponent],
      providers: [TreeService, ClassificationsService]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(ClassificationTreeComponent);
      classificationsService = testBed.get(ClassificationsService);
      spyOn(classificationsService, 'putClassification').and.callFake((url, classification) => classification);
      moveNodeEvent = {
        eventName: 'moveNode',
        node: {
          classificationId: 'id4',
          parentId: '',
          parentKey: '',
          _links: {
            self: {
              href: 'url'
            }
          }
        },
        to: {
          parent: {
            classificationId: 'id3',
            key: 'key3'
          }
        }
      };

      dropEvent = {
        event: {
          target: {
            tagName: 'TREE-VIEWPORT'
          }
        },
        element: {
          data: {
            classificationId: 'id3',
            parentId: 'id1',
            parentKey: 'key1'
          }
        }
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
    spyOn(classificationsService, 'getClassification').and.returnValue(new ClassificationDefinition('id4',
      'key4', '', '', 'MANUAL', 'DOMAIN_A', 'TASK', true, '019-04-10T10:23:34.985Z', '2019-04-10T10:23:34.985Z',
      'classification4', 'description', 1, 'level', '', '', '', '', '', '',
      '', '', '', new LinksClassification({ href: '' }, '', '', { href: '' }, { href: '' }, { href: '' })));
    spyOn(component, 'switchTaskanaSpinner');
    const classification = classificationsService.getClassification();
    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');

    await component.onMoveNode(moveNodeEvent);

    expect(classification.parentId).toEqual('id3');
    expect(classification.parentKey).toEqual('key3');
    expect(classificationsService.putClassification).toHaveBeenCalledWith(classification._links.self.href, classification);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(true);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(false);
  });

  it('should be changed the parent classification to root node (onDrop)', async () => {
    spyOn(classificationsService, 'getClassification').and.returnValue(new ClassificationDefinition('id3',
      'key3', 'id1', 'key1', 'MANUAL', 'DOMAIN_A', 'TASK', true, '019-04-10T10:23:34.985Z', '2019-04-10T10:23:34.985Z',
      'classification3', 'description', 1, 'level', '', '', '', '', '', '',
      '', '', '', new LinksClassification({ href: '' }, '', '', { href: '' }, { href: '' }, { href: '' })));
    spyOn(component, 'switchTaskanaSpinner');
    const classification = classificationsService.getClassification();
    expect(classification.parentId).toEqual('id1');
    expect(classification.parentKey).toEqual('key1');

    await component.onDrop(dropEvent);

    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(true);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(false);
  });
});
