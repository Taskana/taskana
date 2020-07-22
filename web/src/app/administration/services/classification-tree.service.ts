import { Injectable } from '@angular/core';
import { TreeNodeModel } from '../models/tree-node';
import { Classification } from '../../shared/models/classification';

@Injectable({
  providedIn: 'root'
})
export class ClassificationTreeService {
  transformToTreeNode(classifications: Classification[]): TreeNodeModel[] {
    const classificationsAsTree: TreeNodeModel[] = classifications
      .map((c) => ({
        ...c,
        children: []
      }))
      .sort((a: TreeNodeModel, b: TreeNodeModel) => a.key.localeCompare(b.key));
    const roots: TreeNodeModel[] = [];
    const children: TreeNodeModel[] = [];
    classificationsAsTree.forEach((item) => {
      const parent = item.parentId;
      const target = !parent ? roots : children[parent] || (children[parent] = []);
      target.push(item);
    });
    roots.forEach((parent) => this.findChildren(parent, children));
    return roots;
  }

  private findChildren(parent: TreeNodeModel, children: TreeNodeModel[]) {
    if (children[parent.classificationId]) {
      parent.children = children[parent.classificationId];
      parent.children.forEach((child) => this.findChildren(child, children));
    }
  }
}
