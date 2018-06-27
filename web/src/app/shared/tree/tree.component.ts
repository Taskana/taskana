import { Component, OnInit, Input, Output, EventEmitter, ViewChild, AfterViewChecked, OnChanges, SimpleChanges } from '@angular/core';
import { TreeNodeModel } from 'app/models/tree-node';

import { KEYS, ITreeOptions, TreeComponent, TreeNode } from 'angular-tree-component';
import { TreeService } from '../../services/tree/tree.service';
import {
  ClassificationCategoriesService
} from 'app/administration/services/classification-categories-service/classification-categories.service';
import { Pair } from 'app/models/pair';

@Component({
  selector: 'taskana-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss']
})
export class TaskanaTreeComponent implements OnInit, AfterViewChecked {


  @ViewChild('tree')
  private tree: TreeComponent;

  @Input() treeNodes: TreeNodeModel;
  @Output() treeNodesChange = new EventEmitter<Array<TreeNodeModel>>();
  @Input() selectNodeId: string;
  @Output() selectNodeIdChanged = new EventEmitter<string>();
  @Input() filterText: string;
  @Input() filterIcon: string;


  private filterTextOld: string
  private filterIconOld: string

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
    animateExpand: true,
    animateSpeed: 20,
    levelPadding: 20
  }

  constructor(private treeService: TreeService, private categoryService: ClassificationCategoriesService) { }

  ngOnInit() {
    this.treeService.getRemovedNodeId().subscribe(value => {
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

    if (this.filterTextOld !== this.filterText ||
      this.filterIconOld !== this.filterIcon) {
      this.filterIconOld = this.filterIcon;
      this.filterTextOld = this.filterText;
      this.filterNodes(this.filterText ? this.filterText : '', this.filterIcon);
      this.manageTreeState();
    }
  }

  onActivate(treeNode: any) {
    this.selectNodeIdChanged.emit(treeNode.node.data.classificationId + '');
  }

  onDeactivate(treeNode: any) {
    this.selectNodeIdChanged.emit(undefined);
  }


  getCategoryIcon(category: string): Pair {
    return this.categoryService.getCategoryIcon(category);
  }

  private selectNode(nodeId: string) {
    if (nodeId) {
      const selectedNode = this.getNode(nodeId)
      if (selectedNode) {
        selectedNode.setIsActive(true)
        this.expandParent(selectedNode);
      }
    }
  }

  private unSelectActiveNode() {
    const activeNode = this.tree.treeModel.getActiveNode();
    activeNode.setIsActive(false);
    activeNode.blur();
  }

  private getNode(nodeId: string) {
    return this.tree.treeModel.getNodeById(nodeId);
  }

  private expandParent(node: TreeNode) {
    if (!node.parent) {
      return
    }
    node.parent.expand();
    this.expandParent(node.parent);
  }

  private filterNodes(text, iconText) {
    this.tree.treeModel.filterNodes((node) => {
      return this.checkNameAndKey(node, text) &&
        this.checkIcon(node, iconText);
    });
  }

  private checkNameAndKey(node: any, text: string): boolean {
    return (node.data.name.toUpperCase().includes(text.toUpperCase())
      || node.data.key.toUpperCase().includes(text.toUpperCase()))
  }
  private checkIcon(node: any, iconText: string): boolean {
    return (node.data.category.toUpperCase() === iconText.toUpperCase()
      || iconText === '')
  }

  private manageTreeState() {
    if (this.filterText === '') {
      this.tree.treeModel.collapseAll();
    }
  }
}

