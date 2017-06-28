import { MonitorPage } from './app.po';

describe('monitor App', () => {
  let page: MonitorPage;

  beforeEach(() => {
    page = new MonitorPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
