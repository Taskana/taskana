import { Links} from '../../model/links';

export class Utils {
  static getSelfRef(links: Array<Links>) { 
    return links.find(l => l.rel === 'self');
  }

  static getTagLinkRef(links: Array<Links>, tag: string) { 
    return links.find(l => l.rel === tag );
  }
}