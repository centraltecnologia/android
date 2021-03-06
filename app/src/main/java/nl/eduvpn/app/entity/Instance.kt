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
package nl.eduvpn.app.entity

/**
 * A configuration for an instance.
 * Created by Daniel Zolnai on 2016-10-07.
 */
data class Instance(
        val baseURI: String,
        val displayName: String?,
        val logoUri: String?,
        val authorizationType: AuthorizationType?,
        val countryCode: String?,
        val isCustom: Boolean,
        val authenticationUrlTemplate: String?,
        val supportContact: List<String>) {

    val sanitizedBaseURI: String
        get() = if (baseURI.endsWith("/")) {
            baseURI.substring(0, baseURI.length - 1)
        } else baseURI

}