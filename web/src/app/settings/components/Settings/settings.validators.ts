import { Settings, SettingTypes } from '../../models/settings';

export const validateSettings = (settings: Settings): string[] => {
  const invalidMembers = [];

  for (let group of settings.schema) {
    for (let member of group.members) {
      const value = settings[member.key];

      if (member.type == SettingTypes.Text || member.type == SettingTypes.Interval) {
        let compareWithMin;
        let compareWithMax;
        switch (member.type) {
          case SettingTypes.Text:
            compareWithMin = value.length;
            compareWithMax = value.length;
            break;
          case SettingTypes.Interval:
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
          invalidMembers.push(member.key);
        }

        if (member.type == SettingTypes.Interval && compareWithMin > compareWithMax) {
          invalidMembers.push(member.key);
        }
      }

      if (member.type == SettingTypes.Json) {
        try {
          JSON.parse(value);
        } catch {
          invalidMembers.push(member.key);
        }
      }
    }
  }
  return invalidMembers;
};
