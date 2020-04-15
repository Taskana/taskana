import { AfterViewChecked,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild } from '@angular/core';

import { ITreeOptions, KEYS, TreeComponent, TreeNode } from 'angular-tree-component';
import { Pair } from 'app/models/pair';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { Select } from '@ngxs/store';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';

import { TreeService } from '../../services/tree.service';
import { TreeNodeModel } from '../../models/tree-node';
import { Classification } from '../../../models/classification';
import { ClassificationDefinition } from '../../../models/classification-definition';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { ClassificationCategoryImages } from '../../../models/customisation';

@Component({
  selector: 'taskana-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss'],
})
export class ClassificationTreeComponent implements OnInit, AfterViewChecked, OnDestroy {
  @Input() treeNodes: Array<TreeNodeModel>;
  @Output() treeNodesChange = new EventEmitter<Array<TreeNodeModel>>();
  @Input() selectNodeId: string;
  @Output() selectNodeIdChanged = new EventEmitter<string>();
  @Input() filterText: string;
  @Input() filterIcon = '';
  @Output() refreshClassification = new EventEmitter<string>();
  @Output() switchTaskanaSpinnerEmit = new EventEmitter<boolean>();
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;
  options: ITreeOptions = {
    displayField: 'name',
    idField: 'classificationId',
    actionMapping: {
      keys: {
        [KEYS.ENTER]: (tree, node, $event) => {
          node.toggleExpanded();
        }
      }
    },
    useVirtualScroll: true,
    animateExpand: true,
    animateSpeed: 20,
    levelPadding: 20,
    allowDrag: true,
    allowDrop: true
  };

  @ViewChild('tree', { static: true })
  private tree: TreeComponent;

  private filterTextOld: string;
  private filterIconOld = '';
  private removedNodeIdSubscription: Subscription;

  constructor(
    private treeService: TreeService,
    private elementRef: ElementRef,
    private classificationsService: ClassificationsService,
  ) {
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event) {
    if (this.checkValidElements(event) && this.tree.treeModel.getActiveNode()) {
      this.unSelectActiveNode();
    }
  }

  ngOnInit() {
    this.removedNodeIdSubscription = this.treeService.getRemovedNodeId().subscribe(value => {
      const removedNode = this.getNode(value);
      if (removedNode.parent) {
        removedNode.parent.collapse();
      }
    });
  }

  ngAfterViewChecked(): void {
    if (this.selectNodeId && !this.tree.treeModel.getActiveNode()) {
      this.selectNode(this.selectNodeId);
    } else if (!this.selectNodeId && this.tree.treeModel.getActiveNode()) {
      this.unSelectActiveNode();
    }

    if (this.filterTextOld !== this.filterText
        || this.filterIconOld !== this.filterIcon) {
      this.filterIconOld = this.filterIcon;
      this.filterTextOld = this.filterText;
      this.filterNodes(this.filterText ? this.filterText : '', this.filterIcon);
      this.manageTreeState();
    }
  }

  onActivate(treeNode: any) {
    this.selectNodeIdChanged.emit(`${treeNode.node.data.classificationId}`);
  }

  onDeactivate(treeNode: any) {
    this.selectNodeIdChanged.emit();
  }

  async onMoveNode($event) {
    this.switchTaskanaSpinner(true);
    const classification = await this.getClassification($event.node.classificationId);
    classification.parentId = $event.to.parent.classificationId;
    classification.parentKey = $event.to.parent.key;
    this.collapseParentNodeIfItIsTheLastChild($event.node);
    await this.updateClassification(classification);
  }

  async onDrop($event) {
    if ($event.event.target.tagName === 'TREE-VIEWPORT') {
      this.switchTaskanaSpinner(true);
      const classification = await this.getClassification($event.element.data.classificationId);
      this.collapseParentNodeIfItIsTheLastChild($event.element.data);
      classification.parentId = '';
      classification.parentKey = '';
      await this.updateClassification(classification);
    }
  }

  getCategoryIcon(category: string): Observable<Pair> {
    return this.categoryIcons$.pipe(map(
      iconMap => (iconMap[category]
        ? new Pair(iconMap[category], category)
        : new Pair(iconMap.missing, 'Category does not match with the configuration'))
    ));
  }

  switchTaskanaSpinner(active: boolean) {
    this.switchTaskanaSpinnerEmit.emit(active);
  }

  ngOnDestroy(): void {
    if (this.removedNodeIdSubscription) {
      this.removedNodeIdSubscription.unsubscribe();
    }
  }

  private selectNode(nodeId: string) {
    if (nodeId) {
      const selectedNode = this.getNode(nodeId);
      if (selectedNode) {
        selectedNode.setIsActive(true);
        this.expandParent(selectedNode);
      }
    }
  }

  private unSelectActiveNode() {
    const activeNode = this.tree.treeModel.getActiveNode();
    delete this.selectNodeId;
    activeNode.setIsActive(false);
    activeNode.blur();
  }

  private getNode(nodeId: string) {
    return this.tree.treeModel.getNodeById(nodeId);
  }

  private expandParent(node: TreeNode) {
    if (!node.parent || node.isRoot) {
      return;
    }
    node.parent.expand();
    this.expandParent(node.parent);
  }

  private filterNodes(text, iconText) {
    this.tree.treeModel.filterNodes(node => this.checkNameAndKey(node, text)
        && this.checkIcon(node, iconText));
  }

  private checkNameAndKey(node: any, text: string): boolean {
    return (node.data.name.toUpperCase().includes(text.toUpperCase())
        || node.data.key.toUpperCase().includes(text.toUpperCase()));
  }

  private checkIcon(node: any, iconText: string): boolean {
    return (node.data.category.toUpperCase() === iconText.toUpperCase()
        || iconText === '');
  }

  private manageTreeState() {
    if (this.filterText === '') {
      this.tree.treeModel.collapseAll();
    }
  }

  private checkValidElements(event): boolean {
    return (this.elementRef.nativeElement.contains(event.target)
        || this.elementRef.nativeElement === event.target)
        && (event.target.localName === 'tree-viewport'
            || event.target.localName === 'taskana-tree');
  }

  private getClassification(classificationId: string): Promise<ClassificationDefinition> {
    return this.classificationsService.getClassification(classificationId);
  }

  private async updateClassification(classification: Classification) {
    await this.classificationsService.putClassification(classification._links.self.href, classification);
    this.refreshClassification.emit(classification.key);
    this.switchTaskanaSpinner(false);
  }

  private collapseParentNodeIfItIsTheLastChild(node: any) {
    if (node.parentId.length > 0 && this.getNode(node.parentId) && this.getNode(node.parentId).children.length < 2) {
      this.tree.treeModel.update();
      this.getNode(node.parentId).collapse();
    }
  }
}
