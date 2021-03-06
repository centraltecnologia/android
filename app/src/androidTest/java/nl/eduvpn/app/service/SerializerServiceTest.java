/*
 *  This file is part of eduVPN.
 *
 *     eduVPN is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     eduVPN is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with eduVPN.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.eduvpn.app.service;

import android.net.Uri;
import android.util.Pair;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import nl.eduvpn.app.entity.AuthorizationType;
import nl.eduvpn.app.entity.DiscoveredAPI;
import nl.eduvpn.app.entity.Instance;
import nl.eduvpn.app.entity.InstanceList;
import nl.eduvpn.app.entity.KeyPair;
import nl.eduvpn.app.entity.Organization;
import nl.eduvpn.app.entity.OrganizationList;
import nl.eduvpn.app.entity.Profile;
import nl.eduvpn.app.entity.SavedAuthState;
import nl.eduvpn.app.entity.SavedKeyPair;
import nl.eduvpn.app.entity.SavedProfile;
import nl.eduvpn.app.entity.Settings;
import nl.eduvpn.app.entity.TranslatableString;
import nl.eduvpn.app.entity.message.Maintenance;
import nl.eduvpn.app.entity.message.Message;
import nl.eduvpn.app.entity.message.Notification;
import nl.eduvpn.app.utils.TTLCache;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the serializer service.
 * Created by Daniel Zolnai on 2016-10-18.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SerializerServiceTest {

    private SerializerService _serializerService;

    @Before
    public void before() {
        _serializerService = new SerializerService();
    }

    @Test
    public void testAppSettingsSerialization() throws SerializerService.UnknownFormatException {
        Settings settings = new Settings(true, true);
        JSONObject jsonObject = _serializerService.serializeAppSettings(settings);
        Settings deserializedSettings = _serializerService.deserializeAppSettings(jsonObject);
        assertEquals(settings.forceTcp(), deserializedSettings.forceTcp());
        assertEquals(settings.useCustomTabs(), deserializedSettings.useCustomTabs());
        settings = new Settings(false, false);
        jsonObject = _serializerService.serializeAppSettings(settings);
        deserializedSettings = _serializerService.deserializeAppSettings(jsonObject);
        assertEquals(settings.forceTcp(), deserializedSettings.forceTcp());
        assertEquals(settings.useCustomTabs(), deserializedSettings.useCustomTabs());
    }

    @Test
    public void testProfileSerialization() throws SerializerService.UnknownFormatException {
        Profile profile = new Profile("displayName", "profileId");
        JSONObject serializedProfile = _serializerService.serializeProfile(profile);
        Profile deserializedProfile = _serializerService.deserializeProfile(serializedProfile);
        assertEquals(profile.getDisplayName(), deserializedProfile.getDisplayName());
        assertEquals(profile.getProfileId(), deserializedProfile.getProfileId());
    }

    @Test
    public void testInstanceSerialization() throws SerializerService.UnknownFormatException {
        Instance instance = new Instance("baseUri", "displayName", "logoUri", AuthorizationType.Distributed, "HU", true, null, Arrays.asList("mailto:user@test.example.com", "tel:+0011223344659898"));
        JSONObject serializedInstance = _serializerService.serializeInstance(instance);
        Instance deserializedInstance = _serializerService.deserializeInstance(serializedInstance);
        assertEquals(instance.getDisplayName(), deserializedInstance.getDisplayName());
        assertEquals(instance.getBaseURI(), deserializedInstance.getBaseURI());
        assertEquals(instance.getLogoUri(), deserializedInstance.getLogoUri());
        assertEquals(instance.getAuthorizationType(), deserializedInstance.getAuthorizationType());
        assertEquals(instance.isCustom(), deserializedInstance.isCustom());
        assertEquals(instance.getCountryCode(), deserializedInstance.getCountryCode());
        assertEquals(instance.getSupportContact(), deserializedInstance.getSupportContact());
    }

    @Test
    public void testDiscoveredAPISerialization() throws SerializerService.UnknownFormatException {
        DiscoveredAPI discoveredAPI = new DiscoveredAPI("base_uri", "auth_endpoint", "token_endpoint");
        JSONObject serializedDiscoveredAPI = _serializerService.serializeDiscoveredAPI(discoveredAPI);
        DiscoveredAPI deserializedDiscoveredAPI = _serializerService.deserializeDiscoveredAPI(serializedDiscoveredAPI);
        assertEquals(discoveredAPI.getAuthorizationEndpoint(), deserializedDiscoveredAPI.getAuthorizationEndpoint());
        assertEquals(discoveredAPI.getApiBaseUri(), deserializedDiscoveredAPI.getApiBaseUri());
        assertEquals(discoveredAPI.getTokenEndpoint(), deserializedDiscoveredAPI.getTokenEndpoint());
    }

    @Test
    public void testInstanceListSerialization() throws SerializerService.UnknownFormatException {
        Instance instance1 = new Instance("baseUri", "displayName", "logoUri", AuthorizationType.Distributed, "SK", true, "https://example.com/template", new ArrayList<>());
        Instance instance2 = new Instance("baseUri2", "displayName2", "logoUri2", AuthorizationType.Local, "BG", true, null, Arrays.asList("mailto:support@example.com", "www.example.com/support"));
        InstanceList instanceList = new InstanceList(Arrays.asList(instance1, instance2), 231);
        JSONObject serializedInstanceList = _serializerService.serializeInstanceList(instanceList);
        InstanceList deserializedInstanceList = _serializerService.deserializeInstanceList(serializedInstanceList);
        assertEquals(instanceList.getSequenceNumber(), deserializedInstanceList.getSequenceNumber());
        assertEquals(instanceList.getInstanceList().size(), deserializedInstanceList.getInstanceList().size());
        assertEquals(instanceList.getInstanceList().get(0).getDisplayName(), deserializedInstanceList.getInstanceList().get(0).getDisplayName());
        assertEquals(instanceList.getInstanceList().get(0).getBaseURI(), deserializedInstanceList.getInstanceList().get(0).getBaseURI());
        assertEquals(instanceList.getInstanceList().get(0).getLogoUri(), deserializedInstanceList.getInstanceList().get(0).getLogoUri());
        assertEquals(instanceList.getInstanceList().get(0).getAuthenticationUrlTemplate(), deserializedInstanceList.getInstanceList().get(0).getAuthenticationUrlTemplate());
        assertEquals(instanceList.getInstanceList().get(1).getDisplayName(), deserializedInstanceList.getInstanceList().get(1).getDisplayName());
        assertEquals(instanceList.getInstanceList().get(1).getBaseURI(), deserializedInstanceList.getInstanceList().get(1).getBaseURI());
        assertEquals(instanceList.getInstanceList().get(1).getLogoUri(), deserializedInstanceList.getInstanceList().get(1).getLogoUri());
        assertEquals(instanceList.getInstanceList().get(1).getAuthenticationUrlTemplate(), deserializedInstanceList.getInstanceList().get(1).getAuthenticationUrlTemplate());
    }

    @Test
    public void testMessageListSerialization() throws SerializerService.UnknownFormatException {
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Maintenance maintenance = new Maintenance(utcCalendar.getTime(), utcCalendar.getTime(), utcCalendar.getTime());
        Notification notification = new Notification(utcCalendar.getTime(), "Example notification");
        List<Message> messageList = Arrays.asList(maintenance, notification);
        JSONObject serializedList = _serializerService.serializeMessageList(messageList, "system_messages");
        List<Message> deserializedList = _serializerService.deserializeMessageList(serializedList, "system_messages");
        assertEquals(messageList.size(), deserializedList.size());
        assertEquals(_norm(messageList.get(0).getDate()), _norm(deserializedList.get(0).getDate()));
        assertEquals(_norm(((Maintenance)messageList.get(0)).getStart()), _norm(((Maintenance)deserializedList.get(0)).getStart()));
        assertEquals(_norm(((Maintenance)messageList.get(0)).getEnd()), _norm(((Maintenance)deserializedList.get(0)).getEnd()));
        assertEquals(_norm(messageList.get(1).getDate()), _norm(deserializedList.get(1).getDate()));
        assertEquals(((Notification)messageList.get(1)).getContent(), ((Notification)deserializedList.get(1)).getContent());
    }

    @Test
    public void testTTLCacheSerialization() throws SerializerService.UnknownFormatException {
        TTLCache<DiscoveredAPI> cache = new TTLCache<>(100);
        DiscoveredAPI discoveredAPI1 = new DiscoveredAPI("baseuri1", "authendpoint1", "tokenendpoint1");
        DiscoveredAPI discoveredAPI2 = new DiscoveredAPI("baseuri2", "authendpoint2", "tokenendpoint2");
        cache.put("key1", discoveredAPI1);
        cache.put("key2", discoveredAPI2);
        JSONObject serializedCache = _serializerService.serializeDiscoveredAPITTLCache(cache);
        TTLCache<DiscoveredAPI> deserializedCache = _serializerService.deserializeDiscoveredAPITTLCache(serializedCache);
        assertEquals(cache.getPurgeAfterSeconds(), deserializedCache.getPurgeAfterSeconds());
        assertEquals(cache.getEntries().size(), deserializedCache.getEntries().size());
        Iterator<Map.Entry<String, Pair<Date, DiscoveredAPI>>> iterator = cache.getEntries().entrySet().iterator();
        Iterator<Map.Entry<String, Pair<Date, DiscoveredAPI>>> deserializedIterator = deserializedCache.getEntries().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Pair<Date, DiscoveredAPI>> entry = iterator.next();
            Map.Entry<String, Pair<Date, DiscoveredAPI>> deserializedEntry = deserializedIterator.next();
            assertEquals(entry.getKey(), deserializedEntry.getKey());
            assertEquals(entry.getValue().first, deserializedEntry.getValue().first);
            assertEquals(entry.getValue().second.getApiBaseUri(), deserializedEntry.getValue().second.getApiBaseUri());
            assertEquals(entry.getValue().second.getAuthorizationEndpoint(), deserializedEntry.getValue().second.getAuthorizationEndpoint());
            assertEquals(entry.getValue().second.getTokenEndpoint(), deserializedEntry.getValue().second.getTokenEndpoint());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testSavedTokenListSerialization() throws SerializerService.UnknownFormatException {
        Instance instance1 = new Instance("baseUri1", "displayName1", null, AuthorizationType.Distributed, "DE", true, "https://example.com/template", Arrays.asList("mailto:support@example.com", "tel:+00123456789"));
        Instance instance2 = new Instance("baseUri2", "displayName2", null, AuthorizationType.Local, "FR", true, null, new ArrayList<>());
        AuthState state1 = new AuthState(new AuthorizationServiceConfiguration(Uri.parse("http://eduvpn.org/auth"), Uri.parse("http://eduvpn.org/token"), null));
        AuthState state2 = new AuthState(new AuthorizationServiceConfiguration(Uri.parse("http://example.com/auth"), Uri.parse("http://example.com/token"), null));
        SavedAuthState token1 = new SavedAuthState(instance1, state1);
        SavedAuthState token2 = new SavedAuthState(instance2, state2);
        List<SavedAuthState> list = Arrays.asList(token1, token2);
        JSONObject serializedList = _serializerService.serializeSavedAuthStateList(list);
        List<SavedAuthState> deserializedList = _serializerService.deserializeSavedAuthStateList(serializedList);
        assertEquals(list.size(), deserializedList.size());
        for (int i = 0; i < list.size(); ++i) {
            assertEquals(list.get(i).getInstance().getSanitizedBaseURI(), deserializedList.get(i).getInstance().getSanitizedBaseURI());
            assertEquals(list.get(i).getAuthState().getAuthorizationServiceConfiguration().authorizationEndpoint, deserializedList.get(i).getAuthState().getAuthorizationServiceConfiguration().authorizationEndpoint);
            assertEquals(list.get(i).getAuthState().getAuthorizationServiceConfiguration().tokenEndpoint, deserializedList.get(i).getAuthState().getAuthorizationServiceConfiguration().tokenEndpoint);
        }
    }

    @Test
    public void testSavedProfileListSerialization() throws SerializerService.UnknownFormatException {
        Instance instance1 = new Instance("baseUri1", "displayName1", "logoUri1", AuthorizationType.Distributed, "SV", true, "https://example.com/template", Collections.singletonList("mailto:support@example.com"));
        Instance instance2 = new Instance("baseUri2", "displayName2", "logoUri2", AuthorizationType.Local, "CH", true, null, new ArrayList<>());
        Profile profile1 = new Profile("displayName1", "profileId1");
        Profile profile2 = new Profile("displayName2", "profileId2");
        SavedProfile savedProfile1 = new SavedProfile(instance1, profile1, "profileUUID1");
        SavedProfile savedProfile2 = new SavedProfile(instance2, profile2, "profileUUID2");
        List<SavedProfile> list = Arrays.asList(savedProfile1, savedProfile2);
        JSONObject serializedList = _serializerService.serializeSavedProfileList(list);
        List<SavedProfile> deserializedList = _serializerService.deserializeSavedProfileList(serializedList);
        assertEquals(list.size(), deserializedList.size());
        for (int i = 0; i < list.size(); ++i) {
            assertEquals(list.get(i).getInstance().getBaseURI(), deserializedList.get(i).getInstance().getBaseURI());
            assertEquals(list.get(i).getInstance().getDisplayName(), deserializedList.get(i).getInstance().getDisplayName());
            assertEquals(list.get(i).getInstance().getLogoUri(), deserializedList.get(i).getInstance().getLogoUri());
            assertEquals(list.get(i).getInstance().isCustom(), deserializedList.get(i).getInstance().isCustom());
            assertEquals(list.get(i).getInstance().getAuthorizationType(), deserializedList.get(i).getInstance().getAuthorizationType());
            assertEquals(list.get(i).getInstance().getAuthenticationUrlTemplate(), deserializedList.get(i).getInstance().getAuthenticationUrlTemplate());

            assertEquals(list.get(i).getProfile().getDisplayName(), deserializedList.get(i).getProfile().getDisplayName());
            assertEquals(list.get(i).getProfile().getProfileId(), deserializedList.get(i).getProfile().getProfileId());
            assertEquals(list.get(i).getProfileUUID(), deserializedList.get(i).getProfileUUID());
        }
    }

    @Test
    public void testKeyPairSerialization() throws SerializerService.UnknownFormatException {
        KeyPair keyPair = new KeyPair(false, "cert1", "pk1");
        JSONObject serializedKeyPair = _serializerService.serializeKeyPair(keyPair);
        KeyPair deserializedKeyPair = _serializerService.deserializeKeyPair(serializedKeyPair);
        assertEquals(keyPair.isOK(), deserializedKeyPair.isOK());
        assertEquals(keyPair.getCertificate(), deserializedKeyPair.getCertificate());
        assertEquals(keyPair.getPrivateKey(), deserializedKeyPair.getPrivateKey());
        keyPair = new KeyPair(true, "example certificate", "example private key");
        serializedKeyPair = _serializerService.serializeKeyPair(keyPair);
        deserializedKeyPair = _serializerService.deserializeKeyPair(serializedKeyPair);
        assertEquals(keyPair.isOK(), deserializedKeyPair.isOK());
        assertEquals(keyPair.getCertificate(), deserializedKeyPair.getCertificate());
        assertEquals(keyPair.getPrivateKey(), deserializedKeyPair.getPrivateKey());
    }

    @Test
    public void testSavedKeyPairSerialization() throws SerializerService.UnknownFormatException {
        KeyPair keyPair = new KeyPair(false, "cert1", "pk1");
        Instance instance = new Instance("http://example.com/", "example.com", null, AuthorizationType.Distributed, "BE", false, "https://example.com/template", new ArrayList<>());

        SavedKeyPair savedKeyPair = new SavedKeyPair(instance, keyPair);
        JSONObject serializedSavedKeyPair = _serializerService.serializeSavedKeyPair(savedKeyPair);
        SavedKeyPair deserializedSavedKeyPair = _serializerService.deserializeSavedKeyPair(serializedSavedKeyPair);
        assertEquals(keyPair.isOK(), deserializedSavedKeyPair.getKeyPair().isOK());
        assertEquals(keyPair.getCertificate(), deserializedSavedKeyPair.getKeyPair().getCertificate());
        assertEquals(keyPair.getPrivateKey(), deserializedSavedKeyPair.getKeyPair().getPrivateKey());
        assertEquals(savedKeyPair.getInstance().getAuthorizationType(), deserializedSavedKeyPair.getInstance().getAuthorizationType());
        assertEquals(savedKeyPair.getInstance().getBaseURI(), deserializedSavedKeyPair.getInstance().getBaseURI());
        assertEquals(savedKeyPair.getInstance().getDisplayName(), deserializedSavedKeyPair.getInstance().getDisplayName());
        assertEquals(savedKeyPair.getInstance().getLogoUri(), deserializedSavedKeyPair.getInstance().getLogoUri());
        assertEquals(savedKeyPair.getInstance().isCustom(), deserializedSavedKeyPair.getInstance().isCustom());
        assertEquals(savedKeyPair.getInstance().getAuthenticationUrlTemplate(), deserializedSavedKeyPair.getInstance().getAuthenticationUrlTemplate());
        keyPair = new KeyPair(true, "example certificate", "example private key");
        instance = new Instance("http://something.else/", "something.else", "http://www.example.com/logo", AuthorizationType.Local, "UK", true, null, Arrays.asList("mailto:support@example.com", "tel:+00123456789"));
        savedKeyPair = new SavedKeyPair(instance, keyPair);
        serializedSavedKeyPair = _serializerService.serializeSavedKeyPair(savedKeyPair);
        deserializedSavedKeyPair = _serializerService.deserializeSavedKeyPair(serializedSavedKeyPair);
        assertEquals(keyPair.isOK(), deserializedSavedKeyPair.getKeyPair().isOK());
        assertEquals(keyPair.getCertificate(), deserializedSavedKeyPair.getKeyPair().getCertificate());
        assertEquals(keyPair.getPrivateKey(), deserializedSavedKeyPair.getKeyPair().getPrivateKey());
        assertEquals(savedKeyPair.getInstance().getAuthorizationType(), deserializedSavedKeyPair.getInstance().getAuthorizationType());
        assertEquals(savedKeyPair.getInstance().getBaseURI(), deserializedSavedKeyPair.getInstance().getBaseURI());
        assertEquals(savedKeyPair.getInstance().getDisplayName(), deserializedSavedKeyPair.getInstance().getDisplayName());
        assertEquals(savedKeyPair.getInstance().getLogoUri(), deserializedSavedKeyPair.getInstance().getLogoUri());
        assertEquals(savedKeyPair.getInstance().isCustom(), deserializedSavedKeyPair.getInstance().isCustom());
        assertEquals(savedKeyPair.getInstance().getCountryCode(), deserializedSavedKeyPair.getInstance().getCountryCode());
        assertEquals(savedKeyPair.getInstance().getSupportContact(), deserializedSavedKeyPair.getInstance().getSupportContact());
        assertEquals(savedKeyPair.getInstance().getAuthenticationUrlTemplate(), deserializedSavedKeyPair.getInstance().getAuthenticationUrlTemplate());
    }

    @Test
    public void testSavedKeyPairListSerialization() throws SerializerService.UnknownFormatException {
        KeyPair keyPair1 = new KeyPair(false, "cert1", "pk1");
        Instance instance1 = new Instance("http://example.com/", "example.com", null, AuthorizationType.Distributed, "NL", false, "https://example.com/url_template", new ArrayList<>());
        SavedKeyPair savedKeyPair1 = new SavedKeyPair(instance1, keyPair1);
        KeyPair keyPair2 = new KeyPair(true, "example certificate", "example private key");
        Instance instance2 = new Instance("http://something.else/", "something.else", "http://www.example.com/logo", AuthorizationType.Local, "HU", true, null, Collections.emptyList());
        SavedKeyPair savedKeyPair2 = new SavedKeyPair(instance2, keyPair2);
        List<SavedKeyPair> savedKeyPairList = Arrays.asList(savedKeyPair1, savedKeyPair2);
        JSONObject serializedSavedKeyPairList = _serializerService.serializeSavedKeyPairList(savedKeyPairList);
        List<SavedKeyPair> deserializedSavedKeyPairList = _serializerService.deserializeSavedKeyPairList(serializedSavedKeyPairList);
        assertEquals(savedKeyPairList.size(), deserializedSavedKeyPairList.size());
        for (int i = 0; i < savedKeyPairList.size(); ++i) {
            assertEquals(savedKeyPairList.get(i).getKeyPair().isOK(), deserializedSavedKeyPairList.get(i).getKeyPair().isOK());
            assertEquals(savedKeyPairList.get(i).getKeyPair().getCertificate(), deserializedSavedKeyPairList.get(i).getKeyPair().getCertificate());
            assertEquals(savedKeyPairList.get(i).getKeyPair().getPrivateKey(), deserializedSavedKeyPairList.get(i).getKeyPair().getPrivateKey());
            assertEquals(savedKeyPairList.get(i).getInstance().getAuthorizationType(), deserializedSavedKeyPairList.get(i).getInstance().getAuthorizationType());
            assertEquals(savedKeyPairList.get(i).getInstance().getBaseURI(), deserializedSavedKeyPairList.get(i).getInstance().getBaseURI());
            assertEquals(savedKeyPairList.get(i).getInstance().getDisplayName(), deserializedSavedKeyPairList.get(i).getInstance().getDisplayName());
            assertEquals(savedKeyPairList.get(i).getInstance().getLogoUri(), deserializedSavedKeyPairList.get(i).getInstance().getLogoUri());
            assertEquals(savedKeyPairList.get(i).getInstance().isCustom(), deserializedSavedKeyPairList.get(i).getInstance().isCustom());
            assertEquals(savedKeyPairList.get(i).getInstance().getCountryCode(), deserializedSavedKeyPairList.get(i).getInstance().getCountryCode());
            assertEquals(savedKeyPairList.get(i).getInstance().getSupportContact(), deserializedSavedKeyPairList.get(i).getInstance().getSupportContact());
            assertEquals(savedKeyPairList.get(i).getInstance().getAuthenticationUrlTemplate(), deserializedSavedKeyPairList.get(i).getInstance().getAuthenticationUrlTemplate());
        }
    }

    @Test
    public void testOrganizationListSerialization() throws SerializerService.UnknownFormatException {
        Map<String, String> keywordsMap = new HashMap<>();
        keywordsMap.put("en", "english keyword");
        keywordsMap.put("de", "german keyword");
        keywordsMap.put("nl", "dutch keyword");
        Organization organization1 = new Organization("orgid-1", new TranslatableString("display name - 1"), new TranslatableString(keywordsMap), "https://server.info/url");
        Organization organization2 = new Organization("orgid-2", new TranslatableString("display name - 2"), new TranslatableString("notthesamekeyword"), "https://server.info2/url");
        Organization organization3 = new Organization("orgid-3", new TranslatableString("display name - 3"), new TranslatableString(), "http://server.info/url3");
        List<Organization> organizations = Arrays.asList(organization1, organization2, organization3);
        OrganizationList organizationList = new OrganizationList(12345L, organizations);
        JSONObject serializedOrganizationList = _serializerService.serializeOrganizationList(organizationList);
        OrganizationList deserializedOrganizationList = _serializerService.deserializeOrganizationList(serializedOrganizationList);
        for (int i = 0; i < organizations.size(); ++i) {
            assertEquals(organizations.get(i).getDisplayName(), deserializedOrganizationList.getOrganizationList().get(i).getDisplayName());
            assertEquals(organizations.get(i).getKeywordList(), deserializedOrganizationList.getOrganizationList().get(i).getKeywordList());
            assertEquals(organizations.get(i).getOrgId(), deserializedOrganizationList.getOrganizationList().get(i).getOrgId());
            assertEquals(organizations.get(i).getSecureInternetHome(), deserializedOrganizationList.getOrganizationList().get(i).getSecureInternetHome());
            assertEquals(organizationList.getVersion(), deserializedOrganizationList.getVersion());
        }
    }

    @Test
    public void testInstancesSerialization() throws SerializerService.UnknownFormatException {
        Instance instance1 = new Instance("http://base.uri/", "Display name 1", null, AuthorizationType.Local, "NL", false, "https://example.com/template", new ArrayList<>());
        Instance instance2 = new Instance("http://base.uri/instance2", "Display name 2", null, AuthorizationType.Local, "US", true, "https://eduvpn.org/template?", Arrays.asList("mailto:user@example.com", "tel:+01234567890123"));
        Instance instance3 = new Instance("https://base.uri/3/", "Display name 3", "https://logo.uri/", AuthorizationType.Organization, "SK", false, null, new ArrayList<>());
        List<Instance> instances = Arrays.asList(instance1, instance2, instance3);
        JSONArray serializedInstances = _serializerService.serializeInstances(instances);
        List<Instance> deserializedInstances = _serializerService.deserializeInstances(serializedInstances);
        for (int i = 0; i < deserializedInstances.size(); ++i) {
            assertEquals(instances.get(i).getBaseURI(), deserializedInstances.get(i).getBaseURI());
            assertEquals(instances.get(i).getDisplayName(), deserializedInstances.get(i).getDisplayName());
            assertEquals(instances.get(i).getLogoUri(), deserializedInstances.get(i).getLogoUri());
            assertEquals(instances.get(i).isCustom(), deserializedInstances.get(i).isCustom());
            assertEquals(instances.get(i).getAuthorizationType(), deserializedInstances.get(i).getAuthorizationType());
            assertEquals(instances.get(i).getCountryCode(), deserializedInstances.get(i).getCountryCode());
            assertEquals(instances.get(i).getSupportContact(), deserializedInstances.get(i).getSupportContact());
            assertEquals(instances.get(i).getAuthenticationUrlTemplate(), deserializedInstances.get(i).getAuthenticationUrlTemplate());
        }
    }

    @Test
    public void testProfileListSerialization() throws SerializerService.UnknownFormatException {
        Profile profile1 = new Profile("display-name1", "profile-id1");
        Profile profile2 = new Profile("display-name2", "profile-id2");
        Profile profile3 = new Profile("display-name3", "profile-id3");
        List<Profile> profiles = Arrays.asList(profile1, profile2, profile3);
        JSONObject serializedProfiles = _serializerService.serializeProfileList(profiles);
        List<Profile> deserializedProfiles = _serializerService.deserializeProfileList(serializedProfiles);
        for (int i = 0; i < profiles.size(); ++i) {
            assertEquals(profiles.get(i).getDisplayName(), deserializedProfiles.get(i).getDisplayName());
            assertEquals(profiles.get(i).getProfileId(), deserializedProfiles.get(i).getProfileId());
        }
    }


    /**
     * Removes the milliseconds from a date. Required because the parser does not care about milliseconds.
     *
     * @param input The date to remove the milliseconds from.
     * @return The date without the milliseconds.
     */
    public static Date _norm(Date input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(input);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
