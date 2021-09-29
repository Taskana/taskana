import { Settings, SettingTypes } from '../../models/settings';

export const validateForm = (members: string[][], settings: Settings, groups: string[]): string[] => {
  const invalidMembers = [];

  for (let groupIndex = 0; groupIndex < groups.length; groupIndex++) {
    for (let memberIndex = 0; memberIndex < members[groupIndex].length; memberIndex++) {
      const memberKey = members[groupIndex][memberIndex];
      const member = settings.schema[groups[groupIndex]].members[memberKey];
      const value = settings[memberKey];

      if (member.type == SettingTypes.TEXT || member.type == SettingTypes.INTERVAL) {
        let compareWithMin;
        let compareWithMax;
        switch (member.type) {
          case SettingTypes.TEXT:
            compareWithMin = value.length;
            compareWithMax = value.length;
            break;
          case SettingTypes.INTERVAL:
            compareWithMin = value[0];
            compareWithMax = value[1];
            break;
        }

        let isValid = true;
        if ((member.min || member.min == 0) && member.max) {
          isValid = compareWithMin >= member.min && compareWithMax <= member.max;
        } else if (member.min || member.min == 0) {
          isValid = compareWithMin >= member.min;
        } else if (member.max) {
          isValid = compareWithMax <= member.max;
        }

        if (!isValid) {
          invalidMembers.push(memberKey);
        }

        if (member.type == SettingTypes.INTERVAL && compareWithMin > compareWithMax) {
          invalidMembers.push(memberKey);
        }
      }

      if (member.type == SettingTypes.JSON) {
        try {
          JSON.parse(value);
        } catch {
          invalidMembers.push(memberKey);
        }
      }
    }
  }
  return invalidMembers;
};
