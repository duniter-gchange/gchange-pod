package org.duniter.elasticsearch.gchange.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.duniter.elasticsearch.model.user.UserProfile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class GchangeUserProfile extends UserProfile {

    public static class Fields extends UserProfile.Fields {}

    private String pubkey;
}
