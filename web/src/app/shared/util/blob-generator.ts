import { saveAs } from 'file-saver';

export class BlobGenerator {
  public static saveFile(object: Object, fileName: string) {
    saveAs(new Blob([JSON.stringify(object, null, 2)], { type: 'application/json;charset=UTF-8' }), fileName);
  }
}
